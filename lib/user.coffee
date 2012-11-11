net = require 'net'
fs = require 'fs'
exec= require('child_process').exec

module.exports = class User

	listeners : []

	constructor : (@id, @location, @name) ->
		@port = 5000 + @id
		@startServer()
		@input_fifo = "/tmp/fifos/#{@id}.in.ts"
		@output_fifo = "/tmp/fifos/#{@id}.out.webm"

	update : (@location) ->

	destroy : ->
		@stopServer()

	sendVideo : (res) ->
		transcoder_output = fs.createReadStream(@output_fifo)
		transcoder_output.on "error", (error) =>
			console.log "error while reading output (user:#{@id})", error
		transcoder_output.pipe(res)

	stopServer : ->
		@server.close()

	startServer : ->
		@server = net.createServer (@socket) =>
			console.log "connect #{@id}"
			
			exec "rm -f #{@input_fifo} && mkfifo #{@input_fifo} && rm -f #{@output_fifo} && mkfifo #{@output_fifo}", =>
				child = exec "ffmpeg -y -probesize 8192 -f mpegts -i #{@input_fifo} #{@output_fifo}", (error, stdout, stderr) =>
					console.log "failed to transcode video for user #{@id}:\n#{stderr}" if error
				transcoder_input = fs.createWriteStream(@input_fifo)
				transcoder_input.on "error", (error) =>
					console.log "error while writing input (user:#{@id})", error
				@socket.pipe(transcoder_input)
				
		@server.listen @port, =>
			console.log "Started TCP #{@port}"





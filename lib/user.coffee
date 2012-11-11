net = require 'net'
fs = require 'fs'
exec= require('child_process').exec

module.exports = class User

	listeners : []

	constructor : (@id, @location) ->
		@port = 5000 + @id
		@startServer()
		@input_fifo = "/tmp/fifos/#{@id}.in.ts"
		@output_fifo = "/tmp/fifos/#{@id}.out.mp4"

	update : (@location) ->

	destroy : ->
		@stopServer()

	sendVideo : (res) ->
		fs.createReadStrem(@output_fifo).pipe(res)

	stopServer : ->
		@server.close()

	startServer : ->
		@server = net.createServer (@socket) =>
			console.log "connect #{@id}"
			
			exec "rm -f #{@input_fifo} && mkfifo #{@input_fifo} && rm -f #{@output_fifo} && mkfifo #{@output_fifo}", =>
				child = exec "ffmpeg -y -probesize 8192 -f mpegts -i #{@input_fifo} -c:v copy #{@output_mp4}", (error, stdout, stderr) =>
					console.log "failed to transcode video for user #{@id}:\n#{stderr}" if error
				@socket.pipe(fs.createWriteStream(@input_fifo))
				
		@server.listen @port, =>
			console.log "Started TCP #{@port}"





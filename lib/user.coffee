net = require 'net'
fs = require 'fs'
exec= require('child_process').exec

module.exports = class User

	constructor : (@id, data) ->
		@update(data)
		@port = 5000 + @id
		@listeners = []
		@input_fifo = "/tmp/fifos/#{@id}.in.ts"
		@output_fifo = "/tmp/fifos/#{@id}.out.webm"

	update : (data) ->
		{ @location, @name, @orientation } = data

	destroy : ->
		@stopServer()
	
	to_json: () ->
		{uid: @id, location: @location, name: @name, orientation: @orientation}

	registerStream : (res) ->
		@listeners.push(res)
		res.on "close", () =>
			index = @listeners.indexOf(res)
			@listeners.splice(index, 1) if index != -1

	stopServer : ->
		@server.close()

	startServer : (serverStarted, videoResumed) ->
		@server = net.createServer (socket) =>
			console.log "connect #{@id}"
			if @transcoder?
				@stopTranscoder =>
					@startTranscoder(socket)
					videoResumed?()
			else
				@startTranscoder(socket)
		@server.listen @port, =>
			console.log "Started TCP #{@port}"
			serverStarted?()
			
	startTranscoder : (socket) ->
		exec "rm -f #{@input_fifo} && mkfifo #{@input_fifo} && rm -f #{@output_fifo} && mkfifo #{@output_fifo}", =>
			@transcoder = exec "ffmpeg -y -probesize 8192 -f mpegts -i #{@input_fifo} #{@output_fifo} &>/dev/null", (error) =>
				console.log "failed to transcode video for user #{@id}:\n#{stderr}" if error
			transcoder_input = fs.createWriteStream(@input_fifo)
			transcoder_input.on "error", (error) =>
				console.log "error while writing input (user:#{@id})", error
			socket.pipe(transcoder_input)
			transcoder_output = fs.createReadStream(@output_fifo)
			transcoder_output.on "data", (data) =>
				console.log "received data #{data.length} for #{@listeners.length} listeners"
				listener.write(data, 'binary') for listener in @listeners
			transcoder_output.on "error", (error) =>
				console.log "error while reading output (user:#{@id})", error

	stopTranscoder : (cb) ->
		exec "kill -9 `ps -eo pid,args | grep '#{@input_fifo} #{@output_fifo}' | cut --delimiter ' ' -f 2`", =>		
			response.end() for response in @listeners
			@listeners = []
			cb?()

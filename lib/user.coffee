net = require 'net'
fs = require 'fs'
ffmpeg = require 'basicFFmpeg'
stream = require 'stream'

module.exports = class User

	listeners : []

	constructor : (@id, @location) ->
		@port = 5000 + @id
		@startServer()

	update : (@location) ->

	destroy : ->
		@stopServer()

	sendVideo : (res) ->
		@listeners.push res

	stopServer : ->
  		@server.close()

	startServer : ->
		@stream = new stream.Stream()
		@stream.writable = true
		@stream.write = (data) ->
			for listener in @listeners
				listener.write data, 'binary'
			return true

		@stream.end = (data) ->
			for listener in @listeners
				listener.write data, 'binary'
				listener.close()

		@server = net.createServer (@socket) =>
			console.log "connect"

			@socket.on "close", ->
				console.log "close"
				for listener in @listeners
					listener.end()

			@processor = ffmpeg.createProcessor
				inputStream: @socket
				outputStream: fs.createWriteStream('./output.webm')
				arguments:
					"-f": "webm"
			@processor.on "progress", (bytes) ->
				console.log "converted #{bytes} bytes"
			@processor.execute()

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	



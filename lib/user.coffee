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
		@server = net.createServer (@socket) =>
			console.log "connect"

			@socket.on "close", =>
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
			@processor.on "info", (info) ->
				console.log "info: #{info}"
			@processor.execute()

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	



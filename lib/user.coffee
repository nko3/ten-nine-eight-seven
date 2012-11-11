net = require 'net'
fs = require 'fs'
spawn = require('child_process').spawn

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
			fifo = "/tmp/fifos/#{@id}"
			child = spawn 'ffmpeg', ['-probesize 8192', '-f', 'mpegts', '-i', fifo, 'test.webm']
			child.stdout.on 'data', (data) ->
				console.log "out: #{data}"
			child.stderr.on 'data', (data) ->
				console.log "err: #{data}"
			@socket.pipe(fs.createWriteStream(fifo))

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	



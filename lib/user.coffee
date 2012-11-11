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
			child = spawn 'ffmpeg', ['-i', 'pipe:0', '-f', 'webm', 'test.webm'],
				stdio: [@socket]
			child.stderr.on 'data', (data) ->
				console.log "es ist zu laut für #{data}"

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	



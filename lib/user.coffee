net = require 'net'
fs = require 'fs'

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

	startServer : ->
		@server = net.createServer (@socket) =>
			console.log "connect"

			@socket.on "data", (data) =>
				for listener in @listeners
					listener.write data, 'binary'

			@socket.on "close", =>
				console.log "close"
				for listener in @listeners
					listener.end()

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	stopServer : ->
  		@server.close()



net = require 'net'

module.exports = class User 

	constructor : (@id, @location) ->
		@port = 5000 + @id
		@startServer()

	update : (@location) ->


	destroy : ->
		@stopServer()

	startServer : ->
		@server = net.createServer (socket) ->
			console.log "connect"

			socket.on "data", ->
				console.log "video..."

		@server.listen @port, =>
  			console.log "Started TCP #{@port}"

  	stopServer : ->
  		@server.close()



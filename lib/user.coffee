net = require 'net'

module.exports = class User 

	constructor : (@uid, @location) ->
		@port = 5000 + @uid
		@startServer()

	update : (@location) ->


	destroy : ->
		@stopServer()


	startServer : ->
		@server = net.createServer (socket) ->
 
  		@server.listen @port

  	stopServer : ->
  		@server.close()



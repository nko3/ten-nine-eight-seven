User = require "./user"

module.exports = class Main

	uid : 0
	vid : 0
	users : {}
	viewers : {}

	constructor : (@io) ->
		vid = 0
		viewers = {}

		@io.sockets.on 'connection', (socket) =>
			vid = @vid++
			socket.set 'vid', vid, =>
				console.log "Create viewer: #{vid}"
				@viewers[vid] = socket

				for uid, user of @users
					socket.emit "createUser", {uid: user.id, location: user.location}

			socket.on 'disconnect', =>
				socket.get 'vid', (err, vid) =>
					console.log "Remove viewer: #{vid}"
					delete @viewers[vid]


	createUser : (location) ->
		uid = @uid++
		console.log "Create user: #{uid}"
		user = @users[uid] = new User uid, location

		@sendToViewers "createUser", {uid: user.id, location: user.location}
		{uid: user.id, port: user.port}

	updateUser : (uid, location) ->
		console.log "Update user: #{uid}"
		user = @users[uid]
		user.update location

		@sendToViewers "updateUser", {uid: user.id, location: user.location}
		{uid: user.id}

	removeUser : (uid) ->
		console.log "Remove user: #{uid}"
		user = @users[uid]
		user.destroy()
		delete @users[uid]

		@sendToViewers "removeUser", {uid: user.id}
		{uid : user.id}

	videoUser : (uid, res) ->
		res.writeHead 200,
  			'Content-Type' : 'image/png'
		@users[uid].sendVideo res

	sendToViewers : (event, data) ->
		for vid, socket of @viewers
			socket.emit event, data

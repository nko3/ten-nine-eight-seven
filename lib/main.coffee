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
					socket.emit "createUser", user.to_json()

			socket.on 'disconnect', =>
				socket.get 'vid', (err, vid) =>
					console.log "Remove viewer: #{vid}"
					delete @viewers[vid]


	createUser : (data, res) ->
		uid = @uid++
		console.log "Create user: #{uid}"
		user = @users[uid] = new User uid, data
		user.startServer =>
			@sendToViewers "createUser", user.to_json()
			res.send {uid: user.id, port: user.port}
		, =>
			@sendToViewers "videoResumed", {uid: user.id}

	updateUser : (uid, data) ->
		console.log "Update user: #{uid}"
		user = @users[uid]
		user.update data

		@sendToViewers "updateUser", user.to_json()
		{uid: user.id}

	removeUser : (uid) ->
		console.log "Remove user: #{uid}"
		if user = @users[uid]
			user.destroy()
			delete @users[uid]

		@sendToViewers "removeUser", {uid: user.id}
		{uid : user.id}

	streamVideo : (uid, res) ->
		res.writeHead 200,
  		'Content-Type' : 'video/webm'
		@users[uid].registerStream res

	sendToViewers : (event, data) ->
		for vid, socket of @viewers
			socket.emit event, data

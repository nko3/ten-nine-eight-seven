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
					socket.emit "createUser", {uid: uid, location: @users[uid].location}

			socket.on 'disconnect', =>
				socket.get 'vid', (err, vid) =>
					console.log "Remove viewer: #{vid}"
					delete @viewers[vid]


	createUser : (location) ->
		uid = @uid++
		console.log "Create user: #{uid}"
		@users[uid] = new User location

		@sendToViewers "createUser", {uid: uid, location: @users[uid].location}

		{uid: uid, created: true}

	updateUser : (uid, location) ->
		console.log "Update user: #{uid}"
		user = @users[uid]
		user.update uid, location
		{uid: uid, updated: true}

	removeUser : (uid) ->
		console.log "Remove user: #{uid}"

		@sendToViewers "removeUser", {uid: uid}

		delete @users[uid]

		{uid : uid, removed: true}

	sendToViewers : (event, data) ->
		for vid, socket of @viewers
			socket.emit event, data

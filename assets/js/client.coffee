class Client

	defaultPosition : [-34.397, 150.644]
	clients : {}
	
	constructor : ->
		@requestPosition =>
			@paintMap()


	requestPosition: (cb) ->
		if navigator.geolocation
			# Call getCurrentPosition with success and failure callbacks
			navigator.geolocation.getCurrentPosition (position) =>
				@position = [position.coords.latitude, position.coords.longitude]
				cb?()
			, (error) =>
				@position = @defaultPosition
				cb?()
		else
			@position = @defaultPosition
			cb?()

	paintUser: (position, name) ->
		image = '/images/location.png';
		result = 
			marker: new google.maps.Marker
				position: position
				map: @map
				icon: image
			label: new Label
				map: @map
				position: position
				text: name

	paintMap : () ->
		viewerPosition = new google.maps.LatLng(@position[0], @position[1])
		options = 
			zoom: 19
			center: viewerPosition
			mapTypeId: google.maps.MapTypeId.ROADMAP
		@map = new google.maps.Map $("#map")[0], options
		@paintUser viewerPosition, "You"


	createUser : (uid, location, orientation, name) ->
		userPosition = new google.maps.LatLng(location.lat, location.lon)
		image = '/images/location.png';
		@clients[uid] = @paintUser userPosition, "user:#{uid}"
		@clients[uid].orientation = orientation
		google.maps.event.addListener @clients[uid].marker, "click", =>
			@showVideo uid

	showVideo : (uid) ->
		@showing = uid
		video = $(document.createElement('video'))
		video.attr 'autoplay', 'autoplay'
		video.attr 'style', "-webkit-transform:rotate(#{@clients[uid].orientation}deg)"
		video.html "<source src='/users/#{uid}/video' type='video/webm'>"
		$('.overlay').append(video);
		$('.overlay').show()
		$('.background').one 'click', =>
			@hideVideo(uid)
	
	hideVideo : (uid) ->
		if @showing == uid
			$('video').each ->
				this.pause()
				delete this
				$(this).remove()
				
			$('.background').unbind 'click'
			$('.overlay').hide()
			@showing = null

	resumeVideo : (uid) ->
		if @showing == uid
			@hideVideo(uid)
			@showVideo(uid)

	removeUser : (uid) ->
		@hideVideo(uid)
		if @clients[uid]
			@clients[uid].marker.setMap null
			@clients[uid].label.setMap null
			delete @clients[uid]

	updateUser : (uid, location, orientation) ->
		position = new google.maps.LatLng(location.lat, location.lon)
		{ marker, label } = @clients[uid]
		marker.setPosition position
		# label.setPosition position
		@clients[uid].orientation = orientation
		$('video').attr 'style', "-webkit-transform:rotate(-#{orientation+90}deg)"

$ ->
	window.client = client = new Client()
	socket = io.connect '/'
	socket.on 'createUser', (data) ->
		client.createUser data.uid, data.location, data.orientation, data.name

	socket.on 'removeUser', (data) ->
		client.removeUser data.uid

	socket.on 'updateUser', (data) ->
		client.updateUser data.uid, data.location, data.orientation

	socket.on 'videoResumed', (data) ->
		client.resumeVideo data.uid

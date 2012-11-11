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


	paintMap : () ->

		viewerPosition = new google.maps.LatLng(@position[0], @position[1])
		options = 
			zoom: 19
			center: viewerPosition
			mapTypeId: google.maps.MapTypeId.ROADMAP

		@map = new google.maps.Map $("#map")[0], options

		image = '/images/location.png';
		clientMarker = new google.maps.Marker
			position: viewerPosition
			map: @map
			icon: image

	createUser : (uid, location) ->
		userPosition = new google.maps.LatLng(location.lat, location.lon)
		image = '/images/location.png';
		@clients[uid] = new google.maps.Marker
			position: userPosition
			map: @map
			icon: image
		google.maps.event.addListener @clients[uid], "click", =>
			@showVideo uid

	showVideo : (uid) ->
		video = $('video')
		source = $(document.createElement('source'))
		source.attr 'src', "/users/#{uid}/video"
		source.attr 'type', "video/webm"
		$('video').append source
		$('.overlay').show()
		$('.background').one 'click', ->
			$('.overlay').hide()

	removeUser : (uid) ->
		if @clients[uid]
			@clients[uid].setMap null
			delete @clients[uid]

$ ->
	client = new Client()
	socket = io.connect '/'
	socket.on 'createUser', (data) ->
		client.createUser data.uid, data.location

	socket.on 'removeUser', (data) ->
		client.removeUser data.uid

	socket.on 'updateUser', (data) ->
		client.removeUser data.uid
		client.createUser data.uid, data.location

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


	createUser : (uid, location, name) ->
		userPosition = new google.maps.LatLng(location.lat, location.lon)
		image = '/images/location.png';
		@clients[uid] = @paintUser userPosition, "user:#{uid} / name:#{name}"
		google.maps.event.addListener @clients[uid].marker, "click", =>
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
			@clients[uid].marker.setMap null
			@clients[uid].label.setMap null
			delete @clients[uid]

$ ->
	client = new Client()
	socket = io.connect '/'
	socket.on 'createUser', (data) ->
		client.createUser data.uid, data.location, data.name

	socket.on 'removeUser', (data) ->
		client.removeUser data.uid

	socket.on 'updateUser', (data) ->
		client.removeUser data.uid
		client.createUser data.uid, data.location

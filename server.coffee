
express = require 'express'

app = express()
server = (require 'http').createServer app
io = (require 'socket.io').listen server
io.set 'log level', 1

Main = require "./lib/main"

server.listen 3000

app.use express.static('public')
app.use express.bodyParser()
app.use require('connect-assets')()

app.engine('.html', require('ejs').__express)

main = new Main io

app.get '/', (req, res) ->
  res.render 'iframe.ejs', {}
app.get '/map', (req, res) ->
  res.render 'index.ejs', {}

app.post '/users/new', (req, res) ->
	main.createUser req.body, res

app.get '/users/:id/video', (req, res) ->
	main.streamVideo req.params.id, res

app.post '/users/:id', (req, res) ->
	res.send main.updateUser req.params.id, req.body

app.delete '/users/:id', (req, res) ->
	res.send main.removeUser req.params.id






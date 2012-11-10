
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

app.engine('.html', require('ejs').__express);


main = new Main io

app.get '/', (req, res) ->
  res.render 'index.ejs', {}

app.post '/users/new', (req, res) ->
	res.send main.createUser req.body.location

app.post '/users/:id', (req, res) ->
	res.send main.updateUser req.params.id, req.body.location

app.delete '/users/:id', (req, res) ->
	res.send main.removeUser req.params.id






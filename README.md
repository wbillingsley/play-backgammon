play-backgammon
===============

A backgammon game using scala, the play-framework, websockets, and d3.js

Play against other humans over the web.  When you visit the homepage, it will randomly
create a new gameId, and set up a game for you. Send the link on the page to whomever
you wish to play against. And start playing.

I might add a simple AI in the next little while too.

How it's built
--------------

The backgammon code itself is in the backgammon package, and is very simple (it's not a hard game).

There is one webpage, which is the game itself. This includes an SVG for the board, and loads a few
pieces of JavaScript. One of these is our client code that opens up a Websocket to the server so that
the browser can send moves, and the server can send back game states.

d3.js is used to render the pieces on the board. This is a very handy JavaScript libarary for rendering
HTML or SVG elements from arrays of JavaScript objects. The game state sent from the server includes the
pieces, and the dice. d3.js then helps us keep the SVG circle elements (for the peices) and the HTML spans
(for the dice) updated appropriately.

A simple couple of JavaScript events on the elements record the piece you clicked on, and when you click
on one of the dice, then sends a move command down the websocket.  The new game state is sent back in
response.

Our scripts are in CoffeeScript, and our stylesheets are in Less -- see app/assets

The dice are just numbers rendered using a dice font.


Internal differences you might notice from the other examples
-------------------------------------------------------------

GameRoom (the Actor that handles receiving moves and broadcasting game states) is based on the ChatRoom 
examples from the Play Framework distribution, but should be slightly simpler and a little bit cleaner
in some ways.

Rather than have the Actor deal with Iteratees on JsValues, it only deals with its event case 
classes. (Trying to keep the interface protocol from leaking into the model.)  It's also been updated to
Concurrent.broadcast (the samples still use a deprecated method), and so that the join method only 
returns a Future of the Enumerator, not the channel (the channel doesn't need to leak out of the room).

The Controller then handles conversion between the event case classes and Json.



Some tradeoffs
--------------

If you haven't got a legal move, you need to try to make an illegal move before it'll progress to the
next player's turn.  This is a little kludge because I didn't want it flipping to the other player's
turn instantly on there being no moves available (it might take the player by surprise).


How to compile and use the game
-------------------------------

1. Download sbt 
2. sbt (to start the sbt console)
3. run
4. visit http://localhost:9000



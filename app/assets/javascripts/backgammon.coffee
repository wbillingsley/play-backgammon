# shortcut - lists the x positions of the board-positions
px = {
	1: 625
	2: 575
	3: 525
	4: 475
	5: 425
	6: 375
	7: 275
	8: 225
	9: 175
	10: 125
	11: 75
	12: 25
	24: 625
	23: 575
	22: 525
	21: 475
	20: 425
	19: 375
	18: 275
	17: 225
	16: 175
	15: 125
	14: 75
	13: 25
}

# The radius of a piece
r = 22

# x position of a piece
cx = (d, i) ->
	if d.home
		685
	else if d.onBar
		325
	else 
		px[d.position]
		
# y position of a piece
cy = (d, i) ->
	piecesBefore = -1
	for j in [0..i]
		piece = Backgammon.gameState.pieces[j]
		if piece.color == d.color
			if (d.home or d.onBar) 
				console.log("homie")
				if (d.home == piece.home) and (d.onBar == piece.onBar)
					piecesBefore += 1
			else if (d.position == piece.position) and (not piece.onBar) and (not piece.home)
				piecesBefore += 1 
					
					
	if d.home or d.onBar
		if d.color == "Red"
			450 - (r + piecesBefore * 5 * 2)
		else
			50 + (r + piecesBefore * 5 * 2)
	else 
		if d.position > 12
			r + piecesBefore * r * 2
		else
			500 - (r + piecesBefore * r * 2)
		


window.Backgammon =

	connect: (game) ->
		@connection = new WebSocket("ws:" + window.location.host + "/gamesocket/" + game + "?red=" + (@color == "Red"))
		@connection.onopen = () -> console.log("Connected to " + game)
		@connection.onerror = (err) -> console.log("Error: " + err) 
		@connection.onmessage = (evt) => 
			@gameState = JSON.parse(evt.data)	
			@updatePieces()		
			@updateDice()
			
			
	updatePieces: () ->
		console.log(@gameState.pieces)
		pieces = d3.select("#pieces").selectAll("circle").data(@gameState.pieces)
		pieces.enter().append("circle").attr("r", r).classed("red", (d) -> d.color == "Red")
		
		pieces.attr("cx", cx).attr("cy", cy).on("click", (d,i) -> 
			Backgammon.select(this)
		)		
		
	updateDice: () ->
		dice = d3.select("#dice").selectAll("a").data(@gameState.dice)
		dice.exit().remove()
		dice.enter().append("a").classed("die", true).on("click", (d,i) -> Backgammon.diceClick(d))
		dice.text((d) -> d).classed("red", (d) -> Backgammon.gameState.toMove == "Red")
		
	deselect: () ->
		d3.select(".selected").classed("selected", false)
		@selected = null
		
	select: (circle) ->
		sel = d3.select(circle)
		piece = sel.datum()
		
		if @color == @gameState.toMove and piece.color == @color and not piece.home
			@deselect()
			@selected = piece
			sel.classed("selected", true)
			
	diceClick: (die) ->
		if @selected?
			from = @selected
			@deselect()
			
			msg = 
				position: from.position
				onBar: from.onBar
				distance: die
				
			console.log(msg)
			@connection.send(JSON.stringify(msg))
			
			
	locationClick: (i) ->
		#if @selected?
		#	from = selected
		#	@deselect()
						
	
		 
		
		
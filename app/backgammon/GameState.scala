package backgammon

class GameState(val toMove:Color, val position:Position, val dice:Seq[Int]) {
  
  /**
   * Applies a move
   */
  def move(move:Move) = {
    if ((dice contains move.distance) && position.isLegal(move)) {
      val diceLeft = dice.diff(Seq(move.distance))
      if (diceLeft.isEmpty) {
        new GameState(toMove.opponent, position.perform(move), GameState.roll)
      } else {
        new GameState(toMove, position.perform(move), diceLeft)
      }
    } else {
      if (hasLegalMoves(move.color)) {
        this
      } else {
        new GameState(toMove.opponent, position.perform(move), GameState.roll)        
      }
    }
  }
  
  override def toString = "(toMove: " + toMove + ", dice" + dice + ", position: " + position + ")"

  def hasLegalMoves(col:Color):Boolean = {
    for (die <- dice; piece <- position.pieces.filter(_.color == col)) {
      if (position.isLegal(new Move(piece, die))) {
        return true
      }
    }
    false
  }
  
}

object GameState {
  
  def newGame = new GameState(Red, Position.start, roll)
  
  def oneDie = 1 + (math.random * 6).toInt
  
  def roll:Seq[Int] = {
    val a = oneDie
    val b = oneDie
    if (a == b) {
      Seq(a, a, a, a)
    } else {
      Seq(a, b)
    }
  }  
  
}


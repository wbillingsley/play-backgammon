package backgammon

trait Color {
  def opponent:Color
}

case object Red extends Color {
  val opponent = Black
}

case object Black extends Color {
  val opponent = Red
}

case class Piece(val color:Color, val position:Int, val onBar:Boolean=false, val home:Boolean=false) {
  
  /**
   * Number of spaces to home
   */
  def distFromHome = {
    if (onBar) 25 
    else if (home) 0
    else color match {
      case Red => position
      case Black => 25 - position
    }
  }
  
  /** Whether this piece is in play on the board. */
  def onBoard = !onBar && !home
  
  /** Whether this piece shares the same board position as another. */
  def sameBoardSpace(other:Piece) = onBoard && other.onBoard && position == other.position
  
} 
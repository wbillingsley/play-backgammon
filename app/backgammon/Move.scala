package backgammon

class Move(val piece:Piece, val distance:Int) {
 
  /** Returns the resulting piece after the move */
  def result = piece.color match {
    case Red => {
      if (piece.onBar) Piece(piece.color, position=25-distance)
      else if (piece.distFromHome > distance) Piece(piece.color, piece.position - distance)
      else Piece(piece.color, 0, home=true)
    }
    case Black => {
      if (piece.onBar) Piece(piece.color, position=distance)
      else if (piece.distFromHome > distance) Piece(piece.color, piece.position + distance)
      else Piece(piece.color, 25, home=true)      
    }
  }
  
  /** Which player is moving */
  def color = piece.color
  
}

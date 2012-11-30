package backgammon

class Position(val pieces: Seq[Piece]) {
  
  override def toString = pieces.toString
  
  /** How many pieces each color has on the bar */
  def piecesOnBar(color:Color) = pieces.count{p => p.color == color && p.onBar }
  
  /** How many pieces this color has on the bar */
  def piecesAt(color:Color, pos:Int) = pieces.count{p => p.color == color && !p.onBar && !p.home && p.position == pos }

  /** How many pieces this color has home */
  def piecesHome(color:Color) = pieces.count{p => p.color == color && p.home }
  
  /** How many pieces are further than dist from home */
  def piecesBefore(color:Color, dist:Int) = pieces.count{p => p.color == color && (p.onBar || p.distFromHome > dist) }
  
  /** How many pieces this color has outside its home board */
  def piecesBeforeHomeBoard(color:Color) = piecesBefore(color, 6)
  
  /** How many pieces each color has in the opponent's home board */
  def piecesBackgammonable(color:Color) = piecesBefore(color, 18)
  
  /** How many pieces this color has on the board */
  def piecesOnBoard(color:Color) = piecesBefore(color, 0)
  
  /** Whether this position indicates this color has won */
  def hasWon(color:Color) = piecesOnBoard(color) <= 0
    
  /** Checks that a move moves pieces from the bar first if there are any. */
  def movesOffBarFirst(move:Move) = move.piece.onBar || piecesOnBar(move.color) == 0
  
  /** Checks that a move only bears off if all pieces are in the home board. */
  def onlyBearsOffIfAllPiecesInHomeBoard(move:Move) = (move.distance < move.piece.distFromHome) || piecesBeforeHomeBoard(move.color) == 0
  
  /** Checks that a bearing off move does not waste any dice distance. */
  def bearOffMustntWaste(move:Move) = move.distance <= move.piece.distFromHome || piecesBefore(move.color, move.distance) == 0
  
  /** Checks that a move on the board doesn't land on two or more opposing pieces. */
  def notBlockedByOpponent(move:Move) = piecesAt(move.color.opponent, move.result.position) < 2
  
  /** Checks that a move is legal in this position. */
  def isLegal(move:Move) = {
    movesOffBarFirst(move) &&
    onlyBearsOffIfAllPiecesInHomeBoard(move) &&
    bearOffMustntWaste(move) &&
    notBlockedByOpponent(move)
  }
  
  /** Returns the new position after the move, bumping any landed-on opponent pieces to the bar. */  
  def perform(move:Move) = {
    val newPiece = move.result
    println(move.piece)
    println(newPiece)
    val idx = pieces.indexOf(move.piece)
    if (idx >= 0) {
      val newPieces = pieces.updated(idx, newPiece).map{ piece => 
      	if (piece.color == move.color.opponent && piece.sameBoardSpace(newPiece)) {
      	  Piece(piece.color, -1, onBar = true)
      	} else {
      	  piece
      	}      	  
      }
      new Position(newPieces)
    } else this      
  }
  
  
  
}

object Position {
  
  val start = new Position(
    (1 to 2).map{i => Piece(Red, 24)} ++
    (1 to 5).map{i => Piece(Red, 13)} ++
    (1 to 3).map{i => Piece(Red, 8)} ++
    (1 to 5).map{i => Piece(Red, 6)} ++
    (1 to 2).map{i => Piece(Black, 1)} ++
    (1 to 5).map{i => Piece(Black, 12)} ++
    (1 to 3).map{i => Piece(Black, 17)} ++
    (1 to 5).map{i => Piece(Black, 19)}     
  )
  
}
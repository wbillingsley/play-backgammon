package models

import akka.actor._
import scala.concurrent.duration._
import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import akka.util.Timeout
import akka.pattern.ask
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import backgammon._
import play.api.libs.iteratee.Concurrent.Channel
import scala.collection.mutable


object GameRoom {
  
  implicit val timeout = Timeout(1 second)
  
  lazy val default = {
    val roomActor = Akka.system.actorOf(Props[GameRoom])
        
    roomActor
  }

  def join(username:String, game:String):scala.concurrent.Future[Enumerator[GREvent]] = {

    (default ? Join(username, game)).map {
      
      case Connected(enumerator) => enumerator
        
      case GRError(error) =>       
        // Send an error and close the socket
        Enumerator[GREvent](GRError(error)).andThen(Enumerator.enumInput(Input.EOF))
         
    }

  }
  
}

class GameRoom extends Actor {
  
  var members = Map.empty[String, Channel[GREvent]]
  
  var gameMembers = new mutable.HashMap[String, mutable.Set[String]] with mutable.MultiMap[String, String]
  
  var games = Map.empty[String, GameState]
  
  def game(username:String) = {
    for ((game, members) <- gameMembers.find{case (game, gmembers) => gmembers.contains(username)}) yield game  
  }
  
  def receive = {
    
    case Join(username, game) => {
      // Create an Enumerator to write to this socket      
      
      val (enumerator, channel) =  Concurrent.broadcast[GREvent]
      if(members.contains(username)) {
        channel push GRError("This username is already used")
      } else {
        if (!games.contains(game)) {
          games = games + (game -> GameState.newGame)
        }
        gameMembers.addBinding(game, username) 
        members = members + (username -> channel)
        
        sender ! Connected(enumerator)
        
        val evt = GRGameState(games(game))
        println(evt)
        channel push evt 
      }
    }
    
    
    case MakeMove(username, move) => {
      for (gameName <- game(username)) {
        val newState = games(gameName).move(move)
        games = games + (gameName -> newState)
        notifyAll(gameName, GRGameState(newState))
      }
    }

    case Quit(username) => {
      members = members - username
      for (game <- game(username)) {
        gameMembers.removeBinding(game, username)
      }
      
      
    }
    
  }
  
  def notifyAll(game: String, event:GREvent) {
    for (member <- gameMembers(game); channel = members(member)) {
      channel push event
    }
  }
  
}

/* An event in the game room */
trait GREvent

/** Start a new game */
case object NewGame extends GREvent

/** Join a game */
case class Join(username: String, game:String) extends GREvent

/** Quit a game */
case class Quit(username: String) extends GREvent

/** Make a move in a game */
case class MakeMove(username: String, move:Move) extends GREvent

/** Notify that someone joined the game */
case class NotifyJoin(username: String) extends GREvent

case class GRGameState(state:GameState) extends GREvent

/** Notify that you've connected (return the enumerator of broadcast events) */
case class Connected(enumerator:Enumerator[GREvent]) extends GREvent

/** An event for when something went wrong */
case class GRError(msg: String) extends GREvent
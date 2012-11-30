package controllers

import play.api._

import play.api.mvc._
import scala.util.Random
import backgammon._
import play.api.libs.json._
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import models._

object Application extends Controller {
  
  val base64 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWQYZabcderfgihjklmnopqrstuvwxyz_("
  
  def randomString = {
    val chars = for (i <- (1 to 8).toArray) yield base64.charAt((math.random * i).toInt)
    new String(chars)
  }
  
  val strlen = 6
  
  def index = Action {
    Redirect(routes.Application.game(randomString, true))    
  }

  def game(gameid:String, red:Boolean) = Action {
    Ok(views.html.index(gameid, if (red) Red else Black))
  }
  
  
  def gameSocket(game:String, red:Boolean) = WebSocket.async[JsValue] { request => 
    
    import scala.concurrent.ExecutionContext.Implicits.global
    val username = randomString 
    
    for (grEnumerator <- GameRoom.join(username, game)) yield {
      
      val jsIteratee = Iteratee.foreach[JsValue] {
	    js => 	      
	      
	    val move = new Move(
	      piece = new Piece(
	          color = if (red) Red else Black,
	          position = (js \ "position").as[Int],
	          onBar = (js \ "onBar").as[Boolean]
	      ),
	      distance = (js \ "distance").as[Int]
	    )
	      
	    GameRoom.default ! MakeMove(username, move)
	  }.mapDone { _ =>
	    GameRoom.default ! Quit(username)
	  }
      
      
      
      val jsEnumerator = grEnumerator.map[JsValue] { evt => 
      	evt match {
      	  case GRGameState(gameState) => Json.obj(
      		"toMove" -> gameState.toMove.toString,
      		"pieces" -> {
	      		for (p <- gameState.position.pieces) yield Json.obj(
	      		  "color" -> p.color.toString,
	      		  "position" -> p.position,
	      		  "onBar" -> p.onBar,
	      		  "home" -> p.home
	      		)
      		},
      		"dice" -> gameState.dice
      	  )      	  
      	  case _ => Json.obj("error" -> "unexpected event")
      	}
      }
      
      (jsIteratee, jsEnumerator)
    }
    
  }
  
}
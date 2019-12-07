package controllers

import java.util.UUID

import javax.inject._
import play.api.mvc._
import de.htwg.se.Chess.Chess
import de.htwg.se.Chess.controller.controllerComponent.{GameStatus, Played}
import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import de.htwg.se.Chess.model.playerComponent.playerBaseImpl.Player
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.libs.streams.ActorFlow

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.actor._

import scala.collection.mutable
import scala.swing.Reactor

@Singleton
class ChessController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {
  val gameController = Chess.controller

  def message = GameStatus.message(gameController.gameStatus)

  def chessAsText = gameController.gridToString + GameStatus.message(gameController.gameStatus)

  var cookieToUid: mutable.Map[String, String] = mutable.Map()
  var uidToPlayer: mutable.Map[String, String] = mutable.Map()

  def about = Action {
    Ok(views.html.index())
  }

  def chess = Action {
    Ok(views.html.chess(gameController, message))
  }

  def chessPolymer = Action {
    Ok(views.html.chessPolymer())
  }

  def turn(row: Int, col: Int, row2: Int, col2: Int) = Action {
    gameController.turn(row, col, row2, col2)
    Ok(gameController.gridToJson)
  }

  def undo = Action {
    gameController.undo
    Ok(views.html.chess(gameController, message))
  }

  def redo = Action {
    gameController.redo
    Ok(views.html.chess(gameController, message))
  }

  def new_game(playerOne: String, playerTwo: String) = Action {
    gameController.createNewGrid(playerOne, playerTwo)
    Ok(gameController.gridToJson)
  }

  def playerAtTurn = Action {
    Ok(gameController.playerAtTurnToString)
  }

  def playerNotAtTurn = Action {
    Ok(gameController.playerNotAtTurnToString)
  }

  def getMessage = Action {
    Ok(message)
  }

  def getGameStatus = Action {
    Ok(gameController.gameStatus.toString)
  }

  def getPossibleMove(row: Int, col: Int) = Action {
    Ok(gameController.getPossibleMove(row, col))
  }

  def gridToJson = Action {
    Ok(gameController.gridToJson)
  }

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connection opened")
      ChessWebSocketActorFactory.create(out)
    }
  }

  object ChessWebSocketActorFactory {
    def create(out: ActorRef) = {
      Props(new ChessWebSocketActor(out))
    }
  }

  class ChessWebSocketActor(out: ActorRef) extends Actor with Reactor {
    listenTo(gameController)

    def receive = {
      case msg: String =>
        out ! (gameController.gridToJson.toString())
        println("Sent Json to Client" + msg)
    }

    reactions += {
      case event: Played => sendJsonToClient
    }

    def sendJsonToClient = {
      println("Received event from Controller")
      out ! (gameController.gridToJson.toString())
    }

  }

}
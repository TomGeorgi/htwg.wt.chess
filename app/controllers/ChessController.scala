package controllers

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
import scala.concurrent.{ ExecutionContext, Future }
import scala.swing.Reactor

@Singleton
class ChessController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {
  val gameController = Chess.controller

  def message = GameStatus.message(gameController.gameStatus)

  def chessAsText = gameController.gridToString + GameStatus.message(gameController.gameStatus)

  var cookieToUid: mutable.Map[String, String] = mutable.Map()
  var uidToPlayer: mutable.Map[String, String] = mutable.Map()

  def offline() = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.offline())
  }

  def about = Action {
    Ok(views.html.index())
  }

  def chess = Action {
    print(gameController.gridToString)
    Ok(views.html.chess(gameController, message))
    //    Ok(chessAsText)
  }

  def vueChess = Action.async {
    Future(Ok(views.html.vueChess(gameController, message)))
  }

  def turn(row: Int, col: Int, row2: Int, col2: Int) = Action {
    print(row, col, row2, col2)
    gameController.turn(row, col, row2, col2)
    print(chessAsText)
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

  /*def select(color: String) = Action {
    var cType = Color.EMPTY
    if (color == "black") {
      cType = Color.BLACK
    } else if (color == "white") {
      cType = Color.WHITE
    }

    var playerColor = ""

    if (selectable_players._1.color == cType) {
      selected_players(color) = selectable_players._1
      playerColor = selectable_players._1.color.toString
    } else if (selectable_players._2.color == cType) {
      selected_players(color) = selectable_players._2
      playerColor = selectable_players._2.color.toString
    }

    Ok(playerColor)
  }

  def join = Action {
    var res = ""
    if (selected_players.isDefinedAt("black")) {
      if (selected_players.get("black") match {
        case Some(res) => res.equals(selectable_players._1)
      }) {
        selected_players("white") = selectable_players._2
        res = selectable_players._2.color.toString
      } else if (selected_players.get("black") match {
        case Some(res) => res.equals(selectable_players._2)
      }) {
        selected_players("white") = selectable_players._1
        res = selectable_players._1.color.toString
      }
    } else if (selected_players.isDefinedAt("white")) {
      if (selected_players.get("white") match {
        case Some(res) => res.equals(selectable_players._1)
      }) {
        selected_players("black") = selectable_players._2
        res = selectable_players._2.color.toString
      } else if (selected_players.get("white") match {
        case Some(res) => res.equals(selectable_players._2)
      }) {
        selected_players("black") = selectable_players._1
        res = selectable_players._1.color.toString
      }
    }

    Ok(res)
  }*/

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connection opened")
      ChessWebSocketActorFactory.create(out)
    }
    /*ActorFlow.actorRef { out =>
      println("Connection opened")

      val cookie = request.cookies.get("PLAY_SESSION") match {
        case Some(c) => c.value
        case None => "ERROR"
      }

      if (cookieToUid.contains(cookie)) {
        println("cookie: " + cookie + " is already present!")

        val uuid = cookieToUid(cookie)
        val playerName = uidToPlayer(uuid)

        if (playerName == gameController.playerAtTurn.name) {
          println("user: " + playerName + " has color " + gameController.playerAtTurn.color)
        } else if (playerName == gameController.playerNotAtTurn.name) {
          println("user: " + playerName + " has color " + gameController.playerNotAtTurn.color)
        } else {
          println("To Much Player at the moment!")
        }
      } else {
        println("Adding cookie")
        val uuid = UUID.randomUUID.toString
        cookieToUid(cookie) = uuid

        //uidToPlayer(uuid) = gameController.playerAtTurn.name
      }

      ChessWebSocketActorFactory.create(out)
    }*/
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
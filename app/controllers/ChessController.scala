package controllers

import javax.inject._

import play.api.mvc._
import de.htwg.se.Chess.Chess
import de.htwg.se.Chess.controller.controllerComponent.GameStatus

@Singleton
class ChessController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val gameController = Chess.controller
  def message = GameStatus.message(gameController.gameStatus)
  def chessAsText =  gameController.gridToString + GameStatus.message(gameController.gameStatus)

  def about = Action {
    Ok(views.html.index())
  }

  def chess = Action {
    print(gameController.gridToString)
    Ok(views.html.chess(gameController, message))
//    Ok(chessAsText)
  }

  def turn(row:Int, col:Int, row2:Int,col2:Int) = Action {
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

  def gridToJson = Action {
    Ok(gameController.gridToJson)
  }
}
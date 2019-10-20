package controllers

import javax.inject._

import play.api.mvc._
import de.htwg.se.Chess.Chess
import de.htwg.se.Chess.controller.controllerComponent.GameStatus

@Singleton
class ChessController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val gameController = Chess.controller
  def chessAsText =  gameController.gridToString + GameStatus.message(gameController.gameStatus)

  def about= Action {
    Ok(views.html.index())
  }

  def chess = Action {
    Ok(chessAsText)
  }

  def set(row:Int, col:Int, row2:Int,col2:Int) = Action {
    gameController.turn(row, col, row2, col2)
    Ok(chessAsText)
  }

  def undo = Action {
    gameController.undo
    Ok(chessAsText)
  }

  def redo = Action {
    gameController.redo
    Ok(chessAsText)
  }
}
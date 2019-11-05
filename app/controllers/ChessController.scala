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

  var pointOneRow = -1
  var pointOneCol = -1
  var pointTwoRow = -1
  var pointTwoCol = -1

  def about = Action {
    Ok(views.html.index())
  }

  def chess = Action {
    print(gameController.gridToString)
    Ok(views.html.chess(gameController, message))
//    Ok(chessAsText)
  }

  def turn(row:Int, col:Int, row2:Int,col2:Int) = Action {
    gameController.turn(row, col, row2, col2)
    Ok(views.html.chess(gameController, message))
  }

  def turn_intern(row:Int, col:Int, row2:Int,col2:Int) {
    gameController.turn(row, col, row2, col2)
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
    pointOneRow = -1
    pointOneCol = -1
    pointTwoRow = -1
    pointTwoCol = -1
    Ok(views.html.chess(gameController, message))
  }

  def point(row: Int, col: Int) = Action {
    if (pointOneRow == -1 && pointOneCol == -1) {
      pointOneRow = row
      pointOneCol = col
    } else if (pointTwoRow == -1 && pointTwoCol == -1) {
      pointTwoRow = row
      pointTwoCol = col
    }

    if (pointOneRow != -1 && pointOneCol != -1 && pointTwoRow != -1 && pointTwoCol != -1) {
      turn_intern(pointOneRow, pointOneCol, pointTwoRow, pointTwoCol)
      pointOneRow = -1
      pointOneCol = -1
      pointTwoRow = -1
      pointTwoCol = -1
    }

    Ok(views.html.chess(gameController, message))
  }
}
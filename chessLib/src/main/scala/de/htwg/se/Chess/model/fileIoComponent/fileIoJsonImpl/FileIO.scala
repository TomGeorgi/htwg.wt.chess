package de.htwg.se.Chess.model.fileIoComponent.fileIoJsonImpl

import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.Chess.ChessModule
import de.htwg.se.Chess.controller.controllerComponent.GameStatus
import de.htwg.se.Chess.controller.controllerComponent.GameStatus.GameStatus
import de.htwg.se.Chess.model.figureComponent.{ Color, FigureFactory }
import de.htwg.se.Chess.model.fileIoComponent.FileIOInterface
import de.htwg.se.Chess.model.gridComponent.{ CellInterface, GridFactory, GridInterface }
import de.htwg.se.Chess.model.playerComponent.{ PlayerFactory, PlayerInterface }
import play.api.libs.json._

import scala.io.Source

class FileIO extends FileIOInterface {

  final val FILE_NAME: String = "Chess.json"

  override def load: Option[(GridInterface, GameStatus, (PlayerInterface, PlayerInterface))] = {

    val source: String = Source.fromFile(FILE_NAME).getLines().mkString
    val json: JsValue = Json.parse(source)
    val size = (json \ "grid" \ "size").get.toString().toInt

    val playerOneName = (json \ "playerOne").get.toString().drop(1).dropRight(1).trim
    val playerTwoName = (json \ "playerTwo").get.toString().drop(1).dropRight(1).trim

    val playerOneColor = Color.fromString((json \ "playerOneColor").get.toString().drop(1).dropRight(1).trim) match {
      case Some(playerOneColorFromString) => playerOneColorFromString
      case None => return None
    }

    val playerTwoColor = Color.fromString((json \ "playerTwoColor").get.toString().drop(1).dropRight(1).trim) match {
      case Some(playerTwoColorFromString) => playerTwoColorFromString
      case None => return None
    }

    val gameStatus = GameStatus.fromString((json \ "state").get.toString.drop(1).dropRight(1).trim) match {
      case Some(gameStatusFromString) => (gameStatusFromString)
      case None => return None
    }

    val injector = Guice.createInjector(new ChessModule)
    var grid = injector.instance[GridFactory].create(size)
    val player1 = injector.instance[PlayerFactory].create(playerOneName, playerOneColor)
    val player2 = injector.instance[PlayerFactory].create(playerTwoName, playerTwoColor)

    val jsV: JsValue = Json.parse((json \\ "cells").head.toString())
    val cellNodes = jsV.validate[List[JsValue]].get
    for (cell <- cellNodes) {
      val row: Int = (cell \ "row").get.toString().toInt
      val col: Int = (cell \ "col").get.toString().toInt

      val color: Color.Value = Color.fromString((cell \ "cellColor").get.toString().drop(1).dropRight(1)) match {
        case Some(cellColorFromString) => cellColorFromString
        case None => Color.EMPTY
      }

      (cell \ "cellTyp").get.toString().drop(1).dropRight(1) match {
        case "PAWN" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createPawn(color)))
        case "ROOK" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createRook(color)))
        case "KNIGHT" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createKnight(color)))
        case "BISHOP" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createBishop(color)))
        case "QUEEN" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createQueen(color)))
        case "KING" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createKing(color)))
        case "None" => grid = grid.set(row, col, None)
      }
    }
    Some((grid, gameStatus, (player1, player2)))
  }

  override def save(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(FILE_NAME))
    pw.write(Json.prettyPrint(controllerToJson(grid, state, player)))
    pw.close
  }

  def controllerToJson(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): JsObject = {
    Json.obj(
      "state" -> JsString(state.toString),
      "playerOne" -> JsString(player._1.name),
      "playerTwo" -> JsString(player._2.name),
      "playerOneColor" -> JsString(player._1.color.toString),
      "playerTwoColor" -> JsString(player._2.color.toString),
      "grid" -> Json.obj(
        "size" -> JsNumber(grid.size),
        "cells" -> Json.toJson(
          for {
            row <- 0 until grid.size
            col <- 0 until grid.size
          } yield cellToJson(row, col, grid.cell(row, col)))))
  }

  def cellToJson(row: Int, col: Int, cell: CellInterface): JsObject = {
    Json.obj(
      "cellColor" -> JsString(cell.value match {
        case Some(res) => res.color.toString
        case None => None.toString
      }),
      "cellTyp" -> JsString(cell.value match {
        case Some(res) => res.typ.toString
        case None => None.toString
      }),
      "row" -> JsNumber(row),
      "col" -> JsNumber(col))
  }

  override def gridToIO(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): JsValue = controllerToJson(grid, state, player)
}

package de.htwg.se.Chess.model.fileIoComponent.fileIoXmlImpl

import com.google.inject.Guice
import de.htwg.se.Chess.ChessModule
import de.htwg.se.Chess.controller.controllerComponent.GameStatus
import de.htwg.se.Chess.controller.controllerComponent.GameStatus.GameStatus
import de.htwg.se.Chess.model.figureComponent.{ Color, FigureFactory }
import de.htwg.se.Chess.model.fileIoComponent.FileIOInterface
import de.htwg.se.Chess.model.gridComponent.{ GridFactory, GridInterface }
import de.htwg.se.Chess.model.playerComponent.{ PlayerFactory, PlayerInterface }
import net.codingwell.scalaguice.InjectorExtensions._
import play.api.libs.json.{ JsValue, Json }

class FileIO extends FileIOInterface {

  final val FILE_NAME: String = "chess.xml"

  override def load: Option[(GridInterface, GameStatus, (PlayerInterface, PlayerInterface))] = {
    val file = scala.xml.XML.loadFile(FILE_NAME)
    val size = (file \\ "grid" \ "@size").text.toInt
    val playerOneName = (file \\ "activePlayer").text.trim
    val playerTwoName = (file \\ "otherPlayer").text.trim
    val playerOneColor = Color.fromString((file \\ "activePlayerColor").text.trim) match {
      case Some(playerOneColorFromString) => playerOneColorFromString
      case None => return None
    }

    val playerTwoColor = Color.fromString((file \\ "otherPlayerColor").text.trim) match {
      case Some(otherPlayerColorFromString) => otherPlayerColorFromString
      case None => return None
    }

    val gameStatus = GameStatus.fromString((file \\ "state").text.trim) match {
      case Some(gameStatusFromString) => (gameStatusFromString)
      case None => return None
    }

    val injector = Guice.createInjector(new ChessModule)
    var grid = injector.instance[GridFactory].create(size)

    val player1 = injector.instance[PlayerFactory].create(playerOneName, playerOneColor)
    val player2 = injector.instance[PlayerFactory].create(playerTwoName, playerTwoColor)

    val cellNode = (file \\ "cell")
    for {
      cell <- cellNode
    } {
      val row: Int = (cell \\ "@row").text.toInt
      val col: Int = (cell \\ "@col").text.toInt
      val color: Color.Value = Color.fromString((cell \\ "cellColor").text.trim) match {
        case Some(cellColorFromString) => (cellColorFromString)
        case None => Color.EMPTY
      }
      (cell \\ "cellFigure").text.trim match {
        case "PAWN" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createPawn(color)))
        case "ROOK" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createRook(color)))
        case "KNIGHT" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createKnight(color)))
        case "BISHOP" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createBishop(color)))
        case "QUEEN" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createQueen(color)))
        case "KING" => grid = grid.set(row, col, Some(injector.instance[FigureFactory].createKing(color)))
        case "None" => grid = grid.set(row, col, None)
      }
    }
    Some(grid, gameStatus, (player1, player2))
  }

  override def save(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): Unit = {
    scala.xml.XML.save(FILE_NAME, controllerToXml(grid, state, player))
  }

  def controllerToXml(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)) = {
    <chess>
      <information>
        <activePlayer>
          { player._1.name }
        </activePlayer>
        <activePlayerColor>
          { player._1.color }
        </activePlayerColor>
        <otherPlayer>
          { player._2.name }
        </otherPlayer>
        <otherPlayerColor>
          { player._2.color }
        </otherPlayerColor>
        <state>
          { state }
        </state>
      </information>{ gridToXml(grid) }
    </chess>
  }

  def gridToXml(grid: GridInterface) = {
    <grid size={ grid.size.toString }>
      {
        for {
          row <- 0 until grid.size
          col <- 0 until grid.size
        } yield cellToXml(grid, row, col)
      }
    </grid>
  }

  def cellToXml(grid: GridInterface, row: Int, col: Int) = {
    <cell row={ row.toString } col={ col.toString }>
      <cellColor>
        {
          grid.cell(row, col).value match {
            case Some(rs) => rs.color.toString
            case None => None.toString
          }
        }
      </cellColor>
      <cellFigure>
        {
          grid.cell(row, col).value match {
            case Some(res) => res.typ.toString
            case None => None.toString
          }
        }
      </cellFigure>
    </cell>
  }

  override def gridToIO(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): JsValue = Json.obj("" -> "")
}

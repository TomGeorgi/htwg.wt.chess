package de.htwg.se.Chess.controller.controllerComponent.controllerMockImpl

import de.htwg.se.Chess.controller.controllerComponent.ControllerInterface
import de.htwg.se.Chess.controller.controllerComponent.GameStatus.{ GameStatus, _ }
import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.gridComponent.GridInterface
import de.htwg.se.Chess.model.gridComponent.gridMockImpl.Grid
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import de.htwg.se.Chess.model.playerComponent.playerBaseImpl.Player
import play.api.libs.json.{ JsString, JsValue, Json }

class Controller(var grid: GridInterface, var player: (PlayerInterface, PlayerInterface)) extends ControllerInterface {

  grid = new Grid(1)

  player = (Player("Player 1", Color.BLACK), Player("Player 2", Color.WHITE))

  override def gameStatus: GameStatus = IDLE

  override def playerAtTurn: PlayerInterface = player._1

  override def playerNotAtTurn: PlayerInterface = player._2

  override def setNextPlayer: Unit = {}

  override def createEmptyGrid(player: (String, String)): Unit = {}

  override def createNewGrid(player: (String, String)): Unit = {}

  override def gridToString: String = grid.toString

  override def playerAtTurnToString: String = player._1.toString

  override def playerNotAtTurnToString: String = player._2.toString

  override def turn(row: Int, col: Int, newRow: Int, newCol: Int): Unit = {}

  override def set(row: Int, col: Int, value: String, color: String): Unit = {}

  override def undo: Unit = {}

  override def redo: Unit = {}

  override def save: Unit = {}

  override def load: Unit = {}

  override def gridToJson: JsValue = JsString("")

  override def getPossibleMove(row: Int, col: Int): JsValue = JsString("")
}

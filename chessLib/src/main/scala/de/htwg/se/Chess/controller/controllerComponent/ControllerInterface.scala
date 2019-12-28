package de.htwg.se.Chess.controller.controllerComponent

import de.htwg.se.Chess.controller.controllerComponent.GameStatus.GameStatus
import de.htwg.se.Chess.model.gridComponent.GridInterface
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import play.api.libs.json.JsValue

import scala.swing.Publisher

trait ControllerInterface extends Publisher {

  def grid: GridInterface
  def gameStatus: GameStatus
  def playerAtTurn: PlayerInterface
  def playerNotAtTurn: PlayerInterface
  def setNextPlayer: Unit
  def createEmptyGrid(player: (String, String)): Unit
  def createNewGrid(player: (String, String)): Unit
  def gridToString: String
  def playerAtTurnToString: String
  def playerNotAtTurnToString: String
  def turn(row: Int, col: Int, newRow: Int, newCol: Int): Unit
  def set(row: Int, col: Int, value: String, color: String): Unit
  def undo: Unit
  def redo: Unit
  def save: Unit
  def load: Unit
  def gridToJson: JsValue
  def getPossibleMove(row: Int, col: Int): JsValue

}

trait ControllerFactory {

  def create(grid: GridInterface, player: (PlayerInterface, PlayerInterface)): ControllerInterface

}

import scala.swing.event.Event

class Played extends Event
case class GridSizeChanged(newSize: Int) extends Event
class CellChanged extends Event
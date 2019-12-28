package de.htwg.se.Chess.model.fileIoComponent

import de.htwg.se.Chess.controller.controllerComponent.GameStatus.GameStatus
import de.htwg.se.Chess.model.gridComponent.GridInterface
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import play.api.libs.json.JsValue

trait FileIOInterface {

  def load: Option[(GridInterface, GameStatus, (PlayerInterface, PlayerInterface))]
  def save(grid: GridInterface, gameStatus: GameStatus, player: (PlayerInterface, PlayerInterface)): Unit
  def gridToIO(grid: GridInterface, state: GameStatus, player: (PlayerInterface, PlayerInterface)): JsValue
}

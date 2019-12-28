package de.htwg.se.Chess.model.playerComponent.playerMockImpl

import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.playerComponent.PlayerInterface

class Player extends PlayerInterface {

  override def name: String = "Player 1"

  override def color: Color.Value = Color.BLACK

}

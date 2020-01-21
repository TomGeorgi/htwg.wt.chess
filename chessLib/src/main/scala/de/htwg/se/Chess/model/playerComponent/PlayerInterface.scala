package de.htwg.se.Chess.model.playerComponent

import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.gridComponent.CellInterface

trait PlayerInterface {

  def name: String
  def color: Color.Value
}

trait PlayerFactory {
  def create(name: String, color: Color.Value): PlayerInterface
}
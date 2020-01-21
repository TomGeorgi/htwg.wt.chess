package de.htwg.se.Chess.model.gridComponent.gridMockImpl

import de.htwg.se.Chess.model.figureComponent.{ Color, Figure }
import de.htwg.se.Chess.model.gridComponent.{ CellInterface, GridInterface }
import play.api.libs.json.{ JsString, JsValue, Json }

class Grid(var size: Int) extends GridInterface {

  size = 1

  override def cell(row: Int, col: Int): CellInterface = EmptyCell

  override def set(row: Int, col: Int, value: Option[Figure]): GridInterface = this

  override def fill(): GridInterface = this

  override var isInCheckColor: Color.Value = Color.EMPTY

  override def isInCheck(colorToCheck: Color.Value): Boolean = false

  override def getAllOtherColorAndCheck(kingPos: (Int, Int), revColor: Color.Value, gridC: GridInterface): Boolean = false

  override def isCheckMate(colorToMate: Color.Value): Boolean = false

  override def gridToJson: JsValue = JsString("")

  override def getPossibleMove(row: Int, col: Int): JsValue = JsString("")

}

object EmptyCell extends CellInterface {

  override def value: Option[Figure] = None

  override def isSet: Boolean = false
}
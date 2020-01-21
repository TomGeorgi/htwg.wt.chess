package de.htwg.se.Chess.model.figureComponent.figureMockImpl

import de.htwg.se.Chess.model.figureComponent.{ Color, Figure, FigureType }
import de.htwg.se.Chess.model.gridComponent.GridInterface

case class Fig(var c: Color.Value) extends Figure {

  override val color: Color.Value = c
  override val typ: FigureType.Value = FigureType.BISHOP

  override def move(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int, grid: GridInterface): Boolean = true

  override def colorReverse(color: Color.Value): Color.Value = Color.EMPTY

  override def moveAll(oldRow: Int, oldCol: Int, grid: GridInterface): List[(Int, Int)] = Nil
}

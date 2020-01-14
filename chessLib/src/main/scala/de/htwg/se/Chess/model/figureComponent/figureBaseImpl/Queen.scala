package de.htwg.se.Chess.model.figureComponent.figureBaseImpl

import com.google.inject.assistedinject.{ Assisted, AssistedInject }
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure, FigureType }
import de.htwg.se.Chess.model.gridComponent.GridInterface

case class Queen @AssistedInject() (@Assisted c: Color.Value) extends Figure {

  override val color: Color.Value = c
  override val typ: FigureType.Value = FigureType.QUEEN

  override def move(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int, grid: GridInterface): Boolean = {
    val revColor: Color.Value = colorReverse(color)
    val moves = (-1, -1) :: (-1, 1) :: (1, -1) :: (1, 1) :: (1, 0) :: (-1, 0) :: (0, 1) :: (0, -1) :: Nil

    for (i <- moves) {
      for (j <- 1 to 8) {
        val move: (Int, Int) = (oldRow + i._1 * j, oldCol + i._2 * j)
        if (move._1 < 8 && move._2 < 8 && move._1 >= 0 && move._2 >= 0) {
          if (move == (newRow, newCol)) {
            if (!wayIsBlocked((oldRow, oldCol), (newRow, newCol), i, grid)) {
              if (grid.cell(move._1, move._2).isSet) {
                grid.cell(move._1, move._2).value match {
                  case Some(res) => res.color match {
                    case `color` => return false
                    case `revColor` => return true
                  }
                  case None => return true
                }
              } else return true
            } else return false
          }
        }
      }
    }
    false
  }

  def wayIsBlocked(oldPlace: (Int, Int), newPlace: (Int, Int), direction: (Int, Int), grid: GridInterface): Boolean = {
    for (i <- 1 to 8) {
      val move: (Int, Int) = (oldPlace._1 + direction._1 * i, oldPlace._2 + direction._2 * i)
      if (move._1 < 8 && move._2 < 8 && move._1 >= 0 && move._2 >= 0) {
        if (move == newPlace) return false
        if (grid.cell(move._1, move._2).isSet) return true
      }
    }
    false
  }

  override def colorReverse(color: Color.Value): Color.Value = color match {
    case Color.WHITE => Color.BLACK
    case Color.BLACK => Color.WHITE
  }

  override def toString: String = {
    color match {
      case Color.BLACK => "\u265B"
      case Color.WHITE => "\u2655"
    }
  }

  override def moveAll(oldRow: Int, oldCol: Int, grid: GridInterface): List[(Int, Int)] = {
    val moves = (-1, -1) :: (-1, 1) :: (1, -1) :: (1, 1) :: (1, 0) :: (-1, 0) :: (0, 1) :: (0, -1) :: Nil
    val revColor = colorReverse(color)
    var list: List[(Int, Int)] = Nil

    for (i <- moves) {
      for (j <- 1 to 8) {
        val move: (Int, Int) = (oldRow + i._1 * j, oldCol + i._2 * j)
        if (move._1 < 8 && move._2 < 8 && move._1 >= 0 && move._2 >= 0) {
          if (!wayIsBlocked((oldRow, oldCol), (move._1, move._2), i, grid)) {
            if (grid.cell(move._1, move._2).isSet) {
              grid.cell(move._1, move._2).value match {
                case Some(res) => res.color match {
                  case `color` =>
                  case `revColor` => list = move :: list
                }
                case None => list = move :: list
              }
            } else list = move :: list
          }
        }
      }
    }
    list
  }
}

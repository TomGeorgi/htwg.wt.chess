package de.htwg.se.Chess.model.figureComponent.figureBaseImpl

import com.google.inject.assistedinject.{ Assisted, AssistedInject }
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure, FigureType }
import de.htwg.se.Chess.model.gridComponent.GridInterface

case class King @AssistedInject() (@Assisted c: Color.Value) extends Figure {
  override val color: Color.Value = c
  override val typ: FigureType.Value = FigureType.KING

  override def move(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int, grid: GridInterface): Boolean = {
    val getMove = (oldRow + 1, oldCol + 1) :: (oldRow + 1, oldCol - 1) :: (oldRow - 1, oldCol - 1) :: (oldRow - 1, oldCol + 1) :: (oldRow + 1, oldCol) :: (oldRow - 1, oldCol) :: (oldRow, oldCol + 1) :: (oldRow, oldCol - 1) :: Nil
    val revColor: Color.Value = colorReverse(color)

    for (valPos <- getMove) {
      if (valPos == (newRow, newCol)) {
        if (grid.cell(valPos._1, valPos._2).isSet) {
          grid.cell(newRow, newCol).value match {
            case Some(res) => res.color match {
              case `color` => return false
              case `revColor` => return true
            }
            case None => return true
          }
        } else return true
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
      case Color.BLACK => "\u265A"
      case Color.WHITE => "\u2654"
    }
  }

  override def moveAll(oldRow: Int, oldCol: Int, grid: GridInterface): List[(Int, Int)] = {
    var getMove = (oldRow + 1, oldCol + 1) :: (oldRow + 1, oldCol - 1) :: (oldRow - 1, oldCol - 1) :: (oldRow - 1, oldCol + 1) :: (oldRow + 1, oldCol) :: (oldRow - 1, oldCol) :: (oldRow, oldCol + 1) :: (oldRow, oldCol - 1) :: Nil
    getMove = getMove.filter(rowcol => rowcol._1 >= 0 && rowcol._1 <= 7 && rowcol._2 >= 0 && rowcol._2 <= 7)
    val revColor: Color.Value = Color.colorReverse(color)
    var list: List[(Int, Int)] = Nil
    for (valPos <- getMove) {
      if (grid.cell(valPos._1, valPos._2).isSet) {
        grid.cell(valPos._1, valPos._2).value match {
          case Some(res) => res.color match {
            case `color` =>
            case `revColor` => list = (valPos._1, valPos._2) :: list
          }
          case None => list = (valPos._1, valPos._2) :: list
        }
      } else list = (valPos._1, valPos._2) :: list
    }
    list
  }
}

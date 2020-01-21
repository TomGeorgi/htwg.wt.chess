package de.htwg.se.Chess.model.figureComponent.figureBaseImpl

import com.google.inject.assistedinject.{ Assisted, AssistedInject }
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure, FigureType }
import de.htwg.se.Chess.model.gridComponent.GridInterface

case class Pawn @AssistedInject() (@Assisted c: Color.Value) extends Figure {

  override val color: Color.Value = c
  override val typ: FigureType.Value = FigureType.PAWN

  override def move(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int, grid: GridInterface): Boolean = {

    val revColor: Color.Value = colorReverse(color)
    val blackBeat = (1, 1) :: (1, -1) :: Nil
    val whiteBeat = (-1, 1) :: (-1, -1) :: Nil

    color match {
      case Color.WHITE => {
        if ((oldRow, oldCol) == (6, oldCol)) {
          if ((oldRow - 2, oldCol) == (newRow, newCol)) {
            if (grid.cell(newRow + 1, newCol).isSet || grid.cell(newRow, newCol).isSet) return false
            return true
          }
        }
        if ((oldRow - 1, oldCol) == (newRow, newCol)) {
          if (grid.cell(newRow, newCol).isSet) return false
          return true
        }
        for (i <- whiteBeat) {
          if ((oldRow + i._1, oldCol + i._2) == (newRow, newCol)) {
            if (grid.cell(newRow, newCol).isSet) {
              grid.cell(newRow, newCol).value match {
                case Some(res) => res.color match {
                  case `color` => return false
                  case `revColor` => return true
                }
                case None => return false
              }
            }
          }
        }
      }
      case Color.BLACK => {
        if ((oldRow, oldCol) == (1, oldCol)) {
          if ((oldRow + 2, oldCol) == (newRow, newCol)) {
            if (grid.cell(newRow - 1, newCol).isSet || grid.cell(newRow, newCol).isSet) return false
            return true
          }
        }
        if ((oldRow + 1, oldCol) == (newRow, newCol)) {
          if (grid.cell(newRow, newCol).isSet) return false
          return true
        }
        for (i <- blackBeat) {
          if ((oldRow + i._1, oldCol + i._2) == (newRow, newCol)) {
            if (grid.cell(newRow, newCol).isSet) {
              grid.cell(newRow, newCol).value match {
                case Some(res) => res.color match {
                  case `color` => return false
                  case `revColor` => return true
                }
                case None => return false
              }
            }
          }
        }
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
      case Color.BLACK => "\u265F"
      case Color.WHITE => "\u2659"
    }
  }

  override def moveAll(oldRow: Int, oldCol: Int, grid: GridInterface): List[(Int, Int)] = {
    val revColor: Color.Value = colorReverse(color)
    val blackBeat = (1, 1) :: (1, -1) :: Nil
    val whiteBeat = (-1, 1) :: (-1, -1) :: Nil

    var list: List[(Int, Int)] = Nil

    color match {
      case Color.WHITE => {
        if ((oldRow, oldCol) == (6, oldCol))
          if (!grid.cell(oldRow - 1, oldCol).isSet && !grid.cell(oldRow - 2, oldCol).isSet) list = (oldRow - 2, oldCol) :: list
        if (!grid.cell(oldRow - 1, oldCol).isSet) list = (oldRow - 1, oldCol) :: list
        for (i <- whiteBeat) {
          var mv = (oldRow + i._1, oldCol + i._2)
          if (mv._1 >= 0 && mv._1 <= 7 && mv._2 >= 0 && mv._2 <= 7) {
            if (grid.cell(mv._1, mv._2).isSet) {
              grid.cell(mv._1, mv._2).value match {
                case Some(res) => res.color match {
                  case `color` =>
                  case `revColor` => {
                    list = (mv._1, mv._2) :: list
                  }
                }
                case None =>
              }
            }
          }
        }
      }
      case Color.BLACK => {
        if ((oldRow, oldCol) == (1, oldCol))
          if (!grid.cell(oldRow + 1, oldCol).isSet && !grid.cell(oldRow + 2, oldCol).isSet) list = (oldRow + 2, oldCol) :: list
        if (!grid.cell(oldRow + 1, oldCol).isSet) list = (oldRow + 1, oldCol) :: list
        for (i <- blackBeat) {
          var mv = (oldRow + i._1, oldCol + i._2)
          if (mv._1 >= 0 && mv._1 <= 7 && mv._2 >= 0 && mv._2 <= 7) {
            if (grid.cell(mv._1, mv._2).isSet) {
              grid.cell(mv._1, mv._2).value match {
                case Some(res) => res.color match {
                  case `color` =>
                  case `revColor` => {
                    list = (mv._1, mv._2) :: list
                  }
                }
                case None =>
              }
            }
          }
        }
      }
    }
    list
  }
}
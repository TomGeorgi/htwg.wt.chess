package de.htwg.se.Chess.model.gridComponent.gridBaseImpl

import com.google.inject.assistedinject.{ Assisted, AssistedInject }
import de.htwg.se.Chess.model.figureComponent.figureBaseImpl._
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure }
import de.htwg.se.Chess.model.gridComponent.{ CellInterface, GridInterface }
import play.api.libs.json.{ JsArray, JsNumber, JsObject, JsString, JsValue, Json }

case class Grid @AssistedInject() (@Assisted cells: Matrix[CellInterface]) extends GridInterface {

  @AssistedInject() def this(@Assisted size: Int) = this(new Matrix[CellInterface](size, Cell(None).asInstanceOf[CellInterface]))

  val size: Int = cells.size

  def cell(row: Int, col: Int): CellInterface = cells.cell(row, col)
  def set(row: Int, col: Int, value: Option[Figure]): GridInterface = copy(cells.replaceCell(row, col, Cell(value).asInstanceOf[CellInterface]))

  def fill(): GridInterface = {
    var fillGrid: GridInterface = set(0, 0, Some(Rook(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 1, Some(Knight(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 2, Some(Bishop(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 3, Some(Queen(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 4, Some(King(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 5, Some(Bishop(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 6, Some(Knight(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(0, 7, Some(Rook(Color.BLACK).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 0, Some(Rook(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 1, Some(Knight(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 2, Some(Bishop(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 3, Some(Queen(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 4, Some(King(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 5, Some(Bishop(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 6, Some(Knight(Color.WHITE).asInstanceOf[Figure]))
    fillGrid = fillGrid.set(7, 7, Some(Rook(Color.WHITE).asInstanceOf[Figure]))

    for {
      col <- 0 until size
    } fillGrid = fillGrid.set(1, col, Some(Pawn(Color.BLACK).asInstanceOf[Figure]))

    for {
      col <- 0 until size
    } fillGrid = fillGrid.set(6, col, Some(Pawn(Color.WHITE).asInstanceOf[Figure]))

    fillGrid
  }

  var isInCheckColor: Color.Value = Color.EMPTY

  def isInCheck(colorToCheck: Color.Value): Boolean = {
    var kingPos: (Int, Int) = (-1, -1)
    for (row <- 0 to 7) {
      for (col <- 0 to 7) {
        if (cell(row, col).value == Some(King(colorToCheck))) {
          kingPos = (row, col)
        }
      }
    }
    val revColor: Color.Value = Color.colorReverse(colorToCheck)
    if (getAllOtherColorAndCheck(kingPos, revColor, this)) {
      isInCheckColor = colorToCheck
      true
    } else {
      false
    }
  }

  def getAllOtherColorAndCheck(kingPos: (Int, Int), revColor: Color.Value, gridC: GridInterface): Boolean = {
    var figureList: List[(Figure, (Int, Int))] = Nil
    for (row <- 0 to 7) {
      for (col <- 0 to 7) {
        cell(row, col).value match {
          case Some(s) => {
            if (s.color == revColor) {
              figureList = (s, (row, col)) :: figureList
            }
          }
          case None =>
        }
      }
    }
    for {
      fig <- figureList
    } if (fig._1.move(fig._2._1, fig._2._2, kingPos._1, kingPos._2, gridC)) return true
    false
  }

  def isCheckMate(colorToMate: Color.Value): Boolean = {
    var figureList: List[(Figure, (Int, Int))] = Nil
    for (row <- 0 to 7) {
      for (col <- 0 to 7) {
        cell(row, col).value match {
          case Some(s) => {
            if (s.color == colorToMate) {
              figureList = (s, (row, col)) :: figureList
            }
          }
          case None =>
        }
      }
    }

    for (i <- figureList) {
      val moves = i._1.moveAll(i._2._1, i._2._2, this)
      for (a <- moves) {
        var testGrid = set(a._1, a._2, Some(i._1))
        testGrid = testGrid.set(i._2._1, i._2._1, None)
        if (!testGrid.isInCheck(colorToMate)) {
          return false
        }
        testGrid = testGrid.set(a._1, a._2, None)
        testGrid = testGrid.set(i._2._1, i._2._1, Some(i._1))
      }
    }
    true
  }

  override def toString: String = {
    val numbers = "y "
    val lineseparator = "  |" + "---+" * (size - 1) + "---|\n"
    val line = "| x " * size + "|\n"
    val letters = ("\n\n    A   B   C   D   E   F   G   H")
    var box = letters + "\n" + (lineseparator + numbers + line) * size + lineseparator
    for {
      row <- 0 until size
      col <- 0 until size
    } box = box.replaceFirst("x", cell(row, col).toString)
    for {
      row <- 0 until size
    } box = box.replaceFirst("y", (size - row).toString)
    box
  }

  override def gridToJson: JsValue = {
    Json.obj("grid" -> Json.obj(
      "size" -> JsNumber(size),
      "cells" -> Json.toJson(
        for {
          row <- 0 until size;
          col <- 0 until size
        } yield {
          Json.obj(
            "row" -> row,
            "col" -> col,
            "cellColor" -> JsString(cell(row, col).value match {
              case Some(res) => res.color.toString
              case None => ""
            }),
            "cell" -> JsString(cell(row, col).value match {
              case Some(res) => res.toString
              case None => ""
            }))
        })))
  }

  override def getPossibleMove(row: Int, col: Int): JsValue = {
    Json.obj(
      "cell" -> Json.obj(
        "moves" -> (cell(row, col).value match {
          case Some(res) => res.moveAll(row, col, this)
          case None => ""
        })))
  }
}
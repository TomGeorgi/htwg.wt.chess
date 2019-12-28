package de.htwg.se.Chess.aview.gui

import de.htwg.se.Chess.controller._

import scala.swing._
import scala.swing.{ Component, Dimension }
import java.awt.{ BasicStroke, Color, Graphics2D, Toolkit }
import java.awt.image.ImageObserver

import de.htwg.se.Chess.aview.gui.FigureImg.getClass
import de.htwg.se.Chess.controller.controllerComponent.ControllerInterface
import de.htwg.se.Chess.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.Chess.model.gridComponent.gridBaseImpl.Grid
import javax.imageio.ImageIO

class Board(val controller: ControllerInterface, var componentSize: Dimension) extends Component with ImageObserver {

  componentSize.setSize((componentSize.height * 0.8) toInt, (componentSize.height * 0.8) toInt)
  preferredSize = new Dimension(componentSize.width, componentSize.height)
  def squareSize = new Dimension(componentSize.width / 8, componentSize.height / 8)

  val board = controller.grid
  val field = board.size - 1
  val whiteColor = new Color(211, 139, 68)
  val blackColor = new Color(254, 206, 157)
  //val highlighted = new Color(232, 172, 112)
  val highlighted = new Color(65, 205, 0)
  var graphic: Graphics2D = ImageIO.read(getClass.getResource("/pieces2.png")).createGraphics()

  override def paintComponent(g: Graphics2D) = {
    listenTo(controller)
    graphic = g
    for {
      row <- 0 to field
      col <- 0 to field
    } {
      val isWhite = (row + col) % 2 == 0
      val coord = getCoord()
      if (7 - coord._1 == row && coord._2 == col) g.setColor(highlighted)
      else if (isWhite) g.setColor(blackColor)
      else g.setColor(whiteColor)

      val currentPos = new Point(row * squareSize.height, col * squareSize.width)

      g.fillRect(currentPos.y, currentPos.x, squareSize.width, squareSize.height)

      paintField(g, currentPos, (row, col))
    }
  }

  def paintField(g: Graphics2D, currPos: Point, pos: (Int, Int)) = {
    controller.grid.cell(pos._1, pos._2).value match {
      case None =>
      case Some(res) => {
        g.drawImage(FigureImg.forFigures(res), currPos.y, currPos.x, this)
      }
    }
  }

  var xCoord = -1
  var yCoord = -1
  def Coord(x: Int, y: Int): Unit = {
    xCoord = x
    yCoord = y
    repaint()
  }

  def getCoord(): (Int, Int) = {
    return (xCoord, yCoord)
  }

  override def imageUpdate(image: Image, i: Int, i1: Int, i2: Int, i3: Int, i4: Int): Boolean = false
}

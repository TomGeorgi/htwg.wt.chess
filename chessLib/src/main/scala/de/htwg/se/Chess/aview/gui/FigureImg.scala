package de.htwg.se.Chess.aview.gui

import de.htwg.se.Chess.model._
import java.awt.Image

import de.htwg.se.Chess.model.figureComponent.figureBaseImpl._
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure, FigureType }
import javax.imageio.ImageIO

object FigureImg {

  val image = ImageIO.read(getClass.getResource("/pieces2.png"))

  val forFigures: Map[Figure, Image] = {

    (for {
      typ <- List(King, Queen, Bishop, Knight, Rook, Pawn)
      col <- List(Color.WHITE, Color.BLACK)
      fig = typ(col)
    } yield {
      val column = fig.typ match {
        case FigureType.KING => 0
        case FigureType.QUEEN => 1
        case FigureType.BISHOP => 2
        case FigureType.KNIGHT => 3
        case FigureType.ROOK => 4
        case FigureType.PAWN => 5
      }

      val line = fig.color match {
        case Color.WHITE => 0
        case Color.BLACK => 1
      }

      fig -> image.getSubimage(column * (image.getWidth() / 6), line * (image.getHeight() / 2), (image.getWidth / 6), (image.getHeight / 2)) //
      // .getScaledInstance(Toolkit.getDefaultToolkit.getScreenSize.width, Toolkit.getDefaultToolkit.getScreenSize.height, Image.SCALE_DEFAULT)
    }).toMap
  }

}

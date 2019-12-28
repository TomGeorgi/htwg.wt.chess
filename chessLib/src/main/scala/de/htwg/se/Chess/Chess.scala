package de.htwg.se.Chess

import de.htwg.se.Chess.aview._
import de.htwg.se.Chess.controller.controllerComponent.{ ControllerFactory, ControllerInterface, Played }
import de.htwg.se.Chess.model.figureComponent.{ Color, Figure }
import de.htwg.se.Chess.model.gridComponent.gridBaseImpl.Grid
import de.htwg.se.Chess.aview.gui.SwingGui
import com.google.inject.Guice
import de.htwg.se.Chess.model.playerComponent.PlayerFactory
import net.codingwell.scalaguice.InjectorExtensions._

import scala.io.StdIn.readLine

object Chess {

  val defaultSize = 8

  var controller = Guice.createInjector(new ChessModule).instance[ControllerFactory].create(
    new Grid(defaultSize).fill(),
    (
      Guice.createInjector(new ChessModule).instance[PlayerFactory].create("Player 1", Color.WHITE),
      Guice.createInjector(new ChessModule).instance[PlayerFactory].create("Player 2", Color.BLACK)))
  val tui = new Tui(controller)
  val gui = new SwingGui(controller)
  controller.publish(new Played)

  def main(args: Array[String]): Unit = {
    var input: String = ""
    do {
      input = readLine()
      tui.processInputLine(input.trim())
    } while (input != "q")
  }
}

package de.htwg.se.Chess.aview

import de.htwg.se.Chess.controller.controllerComponent.{ ControllerInterface, GameStatus, GridSizeChanged, Played }
import de.htwg.se.Chess.controller.controllerComponent.GameStatus._
import com.typesafe.scalalogging.LazyLogging

import scala.swing.Reactor

class Tui(controller: ControllerInterface) extends Reactor with LazyLogging {

  listenTo(controller)

  val player: (String, String) = ("Player 1", "Player 2")

  logger.info("Chess from Tom Georgi and Lukas Rohloff")
  logger.info("Input format: column - row - new column - new row")
  logger.info("column: from A to H")
  logger.info("Row: from 1 to 8")
  logger.info("For More Information type help")

  def processInputLine(input: String): Unit = {
    val in = input.split("[ ]+")
    in(0) match {
      case "test" => logger.info("" + controller.getPossibleMove(0, 0))
      case "q" => System.exit(0)
      case "y" => controller.redo
      case "z" => controller.undo
      case "s" => controller.save
      case "l" => controller.load
      case "set" => processInputMove(in)
      case "emp" => {
        if (in.length >= 3)
          controller.createEmptyGrid((in(1), in(2)))
        else
          controller.createEmptyGrid(player)
      }
      case "help" => logger.info("\n q -> Leaves the game" +
        "\n n -> Start a new Game with Player 1 and Player 2" +
        "\n n - name - name -> Start a new Game with the entered names for player1 and player2" +
        "\n emp -> Start a new Game with an empty Grid" +
        "\n emp - name - name -> Start a new Game with an empty Grid and the entered names for player1 and player2 \n")
      case "n" => {
        if (in.length >= 3)
          controller.createNewGrid((in(1), in(2)))
        else
          controller.createNewGrid(player)
        logger.info("Input format: column - row - new column - new row\n column: from A to H\n Row: from 1 to 8\n For More Information type help")
        printGameTui
      }
      case _ => processInputMove(in)
    }
  }

  def processInputMove(in: Array[String]): Unit = {
    in.toList.filter(c => c != " ").map(c => c.toString) match {
      case placeCol :: placeRow :: newPlaceCol :: newPlaceRow :: Nil =>
        controller.turn(8 - placeRow.toInt, charToValue(placeCol), 8 - newPlaceRow.toInt, charToValue(newPlaceCol))
      case _ :: pC :: pR :: value :: color :: Nil => controller.set(8 - pR.toInt, charToValue(pC), value, color)
      case _ :: pC :: pR :: Nil => controller.set(8 - pR.toInt, charToValue(pC), "_", "_")
      case placeCol :: placeRow :: Nil => logger.info("" + controller.getPossibleMove(8 - placeRow.toInt, charToValue(placeCol)))
      case _ =>
    }
  }

  def charToValue(col: String): Int = {
    col match {
      case "A" | "a" => 0
      case "B" | "b" => 1
      case "C" | "c" => 2
      case "D" | "d" => 3
      case "E" | "e" => 4
      case "F" | "f" => 5
      case "G" | "g" => 6
      case "H" | "h" => 7
    }
  }

  reactions += {
    case event: GridSizeChanged => printGameTui
    case event: Played => printGameTui
  }

  def printGameTui: Unit = {
    logger.info(controller.gridToString)
    //println(controller.gridToString)
    if (controller.gameStatus == NEXT_PLAYER) {
      logger.info("\n" + controller.playerAtTurnToString + GameStatus.message(controller.gameStatus))
    } else if (controller.gameStatus == CHECK_MATE) {
      logger.info("\n" + controller.playerNotAtTurnToString + GameStatus.message(controller.gameStatus))
    } else {
      logger.info("\n" + controller.playerAtTurnToString + GameStatus.message(controller.gameStatus))
    }

  }
}

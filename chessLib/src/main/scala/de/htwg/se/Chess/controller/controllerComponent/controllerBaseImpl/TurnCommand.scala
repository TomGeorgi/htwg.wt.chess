package de.htwg.se.Chess.controller.controllerComponent.controllerBaseImpl

import de.htwg.se.Chess.controller.controllerComponent.GameStatus._
import net.codingwell.scalaguice.InjectorExtensions._
import de.htwg.se.Chess.model._
import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.gridComponent.{ CellFactory, GridInterface }
import de.htwg.se.Chess.model.gridComponent.gridBaseImpl.Grid
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import de.htwg.se.Chess.model.playerComponent.playerBaseImpl.Player
import de.htwg.se.Chess.util.Command

class TurnCommand(oldRow: Int, oldCol: Int, newRow: Int, newCol: Int, controller: Controller) extends Command {

  var memento: (GridInterface, (PlayerInterface, PlayerInterface)) = (controller.grid, controller.player)

  override def doStep: Unit = {
    memento = (controller.grid, controller.player)

    if (controller.gameStatus == CHECK_MATE) {
      return
    }

    val whichPlayer = controller.playerAtTurn
    val oldValue = controller.grid.cell(oldRow, oldCol).value
    var canSet: Boolean = false

    oldValue match {
      case Some(res) => {
        val color = res.color
        if (color == whichPlayer.color) canSet = res.move(oldRow, oldCol, newRow, newCol, controller.grid)
      }
      case None =>
    }

    if (canSet) {
      var gridtoCheck = controller.grid.set(oldRow, oldCol, None)
      gridtoCheck = gridtoCheck.set(newRow, newCol, oldValue)
      if (gridtoCheck.isInCheck(whichPlayer.color)) {
        controller.gameStatus = MOVE_NOT_VALID
      } else {
        if (gridtoCheck.isInCheck(Color.colorReverse(whichPlayer.color))) {
          if (gridtoCheck.isCheckMate(Color.colorReverse(whichPlayer.color))) {
            controller.grid = gridtoCheck
            controller.gameStatus = CHECK_MATE
            return
          }
          gridtoCheck.isInCheckColor = Color.colorReverse(whichPlayer.color)
          controller.grid = gridtoCheck
          controller.setNextPlayer
          controller.gameStatus = CHECK
          return
        }
        controller.grid = gridtoCheck
        controller.setNextPlayer
        controller.gameStatus = NEXT_PLAYER
      }
    } else {
      controller.gameStatus = MOVE_NOT_VALID
    }
  }

  override def undoStep: Unit = {
    val new_memento = (controller.grid, controller.player)
    controller.grid = memento._1
    controller.player = memento._2
    memento = new_memento
  }

  override def redoStep: Unit = {
    val new_memento = (controller.grid, controller.player)
    controller.grid = memento._1
    controller.player = memento._2
    memento = new_memento
  }
}

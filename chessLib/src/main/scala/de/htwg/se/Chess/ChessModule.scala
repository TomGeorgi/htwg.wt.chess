package de.htwg.se.Chess

import com.google.inject.{ AbstractModule, Key }
import com.google.inject.assistedinject.FactoryModuleBuilder
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.Chess.controller.controllerComponent._
import de.htwg.se.Chess.model.figureComponent.figureBaseImpl._
import de.htwg.se.Chess.model.figureComponent.{ Figure, FigureFactory }
import de.htwg.se.Chess.model.gridComponent.{ CellFactory, CellInterface, GridFactory, GridInterface }
import de.htwg.se.Chess.model.gridComponent.gridBaseImpl.{ Cell, Grid }
import de.htwg.se.Chess.model.playerComponent.{ PlayerFactory, PlayerInterface }
import de.htwg.se.Chess.model.playerComponent.playerBaseImpl.Player
import com.google.inject.name.Names
import de.htwg.se.Chess.model.fileIoComponent._

class ChessModule extends AbstractModule with ScalaModule {

  val defaultSize: Int = 8
  val defaultPlayerName = ("Player 1", "Player 2")

  override def configure(): Unit = {
    install(new FactoryModuleBuilder().implement(classOf[PlayerInterface], classOf[Player]).build(classOf[PlayerFactory]))
    install(new FactoryModuleBuilder().implement(classOf[GridInterface], classOf[Grid]).build(classOf[GridFactory]))
    install(new FactoryModuleBuilder().implement(classOf[ControllerInterface], classOf[controllerBaseImpl.Controller]).build(classOf[ControllerFactory]))
    install(new FactoryModuleBuilder().implement(classOf[CellInterface], classOf[Cell]).build(classOf[CellFactory]))
    install(new FactoryModuleBuilder().implement(classOf[Figure], Names.named("Rook"), classOf[Rook])
      .implement(Key.get(classOf[Figure], Names.named("Queen")), classOf[Queen])
      .implement(Key.get(classOf[Figure], Names.named("Bishop")), classOf[Bishop])
      .implement(Key.get(classOf[Figure], Names.named("Knight")), classOf[Knight])
      .implement(Key.get(classOf[Figure], Names.named("Pawn")), classOf[Pawn])
      .implement(Key.get(classOf[Figure], Names.named("King")), classOf[King]).build(classOf[FigureFactory]))
    bind[CellInterface].to[Cell]

    bind[FileIOInterface].to[fileIoJsonImpl.FileIO]
  }
}

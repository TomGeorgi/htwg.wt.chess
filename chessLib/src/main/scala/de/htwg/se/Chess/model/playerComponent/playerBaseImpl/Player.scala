package de.htwg.se.Chess.model.playerComponent.playerBaseImpl

import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted

case class Player @Inject() (@Assisted name: String, @Assisted color: Color.Value) extends PlayerInterface {
  override def toString: String = name
}


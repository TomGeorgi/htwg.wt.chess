package de.htwg.se.Chess.model.gridComponent.gridBaseImpl

import com.google.inject.assistedinject.{ Assisted, AssistedInject }
import de.htwg.se.Chess.model.figureComponent.Figure
import de.htwg.se.Chess.model.gridComponent.CellInterface

case class Cell @AssistedInject() (@Assisted value: Option[Figure]) extends CellInterface {

  @AssistedInject def this() = this(None)

  def isSet: Boolean = value match {
    case Some(_) => true
    case None => false
  }

  override def toString: String = {
    value match {
      case Some(s) => s.toString
      case None => " "
    }
  }
}


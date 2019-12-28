package de.htwg.se.Chess.controller.controllerComponent

object GameStatus extends Enumeration {
  type GameStatus = Value

  val IDLE, NEW_GAME, NEW_GAME_EMPTY, LOADED, SAVED, NEXT_PLAYER, MOVE_NOT_VALID, CHECK_MATE, EN_PASSANT, CHECK, PROMOTION = Value

  val map = Map[GameStatus, String](
    IDLE -> "",
    NEW_GAME -> "\nA new Game was started",
    NEW_GAME_EMPTY -> "\nA new Game with an empty grid was started",
    LOADED -> "\nA new Game was loaded",
    SAVED -> "\nThe Game was saved",
    NEXT_PLAYER -> " is at turn",
    MOVE_NOT_VALID -> ", this move is not valid!",
    CHECK_MATE -> " is CHECKMATE!",
    EN_PASSANT -> " did en passant!",
    CHECK -> " is in check...",
    PROMOTION -> " got a Promotion!",
  )

  def fromString(s: String): Option[GameStatus] = s.trim match {
    case "NEXT_PLAYER" =>
      Some(NEXT_PLAYER)
    case "MOVE_NOT_VALID" =>
      Some(MOVE_NOT_VALID)
    case "CHECK_MATE" =>
      Some(CHECK_MATE)
    case "CHECK" =>
      Some(CHECK)
    case _ =>
      None
  }

  def message(msg: GameStatus) = map(msg)
}

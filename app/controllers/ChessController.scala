package controllers

import javax.inject._
import play.api.mvc._
import de.htwg.se.Chess.Chess
import de.htwg.se.Chess.controller.controllerComponent.{ ControllerInterface, GameStatus, Played }
import de.htwg.se.Chess.model.figureComponent.Color
import de.htwg.se.Chess.model.playerComponent.PlayerInterface
import de.htwg.se.Chess.model.playerComponent.playerBaseImpl.Player
import play.api.libs.json.{ JsString, JsValue, Json }
import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.actor._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers.GoogleTotpInfo
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv

import models.User

import scala.concurrent.{ ExecutionContext, Future }
import scala.swing.Reactor

@Singleton
class ChessController @Inject() ()(
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  authInfoRepository: AuthInfoRepository
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  system: ActorSystem,
  mat: Materializer,
  ec: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  val gameController: ControllerInterface = Chess.controller

  def message = GameStatus.message(gameController.gameStatus)

  def chessAsText = gameController.gridToString + GameStatus.message(gameController.gameStatus)

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  def offline() = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.offline()))
  }

  /*def about = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(views.html.chess.about(request.identity, totpInfoOpt))
    }
  }*/

  def chess = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(views.html.chess(gameController, message, request.identity, totpInfoOpt))
    }
  }

  def vueChess = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(views.html.vueChess(gameController, message, request.identity, totpInfoOpt))
    }
  }

  def turn(row: Int, col: Int, row2: Int, col2: Int) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      gameController.turn(row, col, row2, col2)
      Ok(gameController.gridToJson)
    }
  }

  def undo = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      gameController.undo
      Ok("")
    }
  }

  def redo = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      gameController.redo
      Ok("")
    }
  }

  def new_game(playerOne: String, playerTwo: String) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      gameController.createNewGrid((playerOne, playerTwo))
      Ok(gameController.gridToJson)
    }
  }

  def playerAtTurn = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(gameController.playerAtTurnToString)
    }
  }

  def playerNotAtTurn = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(gameController.playerNotAtTurnToString)
    }
  }

  def getMessage = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(message)
    }
  }

  def getGameStatus = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(gameController.gameStatus.toString)
    }
  }

  def getPossibleMove(row: Int, col: Int) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(gameController.getPossibleMove(row, col))
    }
  }

  def gridToJson = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    authInfoRepository.find[GoogleTotpInfo](request.identity.loginInfo).map { totpInfoOpt =>
      Ok(gameController.gridToJson)
    }
  }

  def socket = WebSocket.acceptOrResult[String, String] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(ChessWebSocketActorFactory.create(user)))
      case HandlerResult(r, None) => Left(r)
    }
  }

  object ChessWebSocketActorFactory {
    def create(user: User)(out: ActorRef) = {
      Props(new ChessWebSocketActor(user, out))
    }
  }

  class ChessWebSocketActor(user: User, out: ActorRef) extends Actor with Reactor {
    listenTo(gameController)

    def receive = {
      case msg: String =>
        println("Message from: " + user.fullName.get)
        participants += user.fullName.get -> out
        sendJsonToClient()
    }

    reactions += {
      case _: Played => sendJsonToClient()
    }

    def sendJsonToClient() = {
      println("Received event from Controller")
      participants.values.foreach(_ ! gameController.gridToJson.toString())
    }

  }

}
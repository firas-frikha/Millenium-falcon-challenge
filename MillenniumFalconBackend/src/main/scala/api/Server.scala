package api

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Server {

  def apply(httpBinder: HttpBinder): Behavior[Input] = Behaviors.setup { actorContext =>

    Behaviors.receiveMessage {
      case Bind(address, port, routes) =>
        actorContext.pipeToSelf(httpBinder.bind(address, port, routes)) {
          case Failure(exception) =>
            LogFailedServerBinding(exception)
          case Success(serverBinding) =>
            LogSuccessfulServerBinding(serverBinding)
        }
        Behaviors.same

      case LogSuccessfulServerBinding(serverBinding) =>
        actorContext.log.info("Server online at http://{}:{}", serverBinding.localAddress.getAddress, serverBinding.localAddress.getPort)
        Behaviors.same

      case LogFailedServerBinding(exception) =>
        actorContext.log.warn("Failed to bind server !: ", exception.getMessage)
        Behaviors.stopped
    }
  }

  sealed trait Input

  case class Bind(address: String, port: Int, routes: HttpRequest => Future[HttpResponse]) extends Input

  case class LogSuccessfulServerBinding(serverBinding: ServerBinding) extends Input

  case class LogFailedServerBinding(exception: Throwable) extends Input
}

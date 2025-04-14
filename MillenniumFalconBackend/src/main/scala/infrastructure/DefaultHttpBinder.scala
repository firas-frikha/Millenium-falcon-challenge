package infrastructure

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import api.HttpBinder

import scala.concurrent.Future

class DefaultHttpBinder()
                       (implicit actorSystem: ActorSystem[_])extends HttpBinder{

  override def bind(address: String, port: Int, routes: HttpRequest => Future[HttpResponse]): Future[Http.ServerBinding] =
    Http().newServerAt(address, port).bind(routes)
}

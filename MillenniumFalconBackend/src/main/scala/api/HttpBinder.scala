package api

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Route

import scala.concurrent.Future

trait HttpBinder {
  def bind(address: String, port: Int, routes: HttpRequest => Future[HttpResponse]): Future[ServerBinding]
}

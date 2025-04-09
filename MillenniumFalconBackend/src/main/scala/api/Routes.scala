package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Routes {
  val routes: Route =
    path("test") {
      get {
        complete("test route")
      }
    }
}

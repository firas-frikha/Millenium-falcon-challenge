package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model.BountyHuntersData

object Routes {
  val routes: Route =
    concat(
      path("test") {
        get {
          complete("test route")
        }
      },
      path("compute") {
        post {
          entity(as[BountyHuntersData]) { entity =>
            complete(entity)
          }
        }
      }
    )
}

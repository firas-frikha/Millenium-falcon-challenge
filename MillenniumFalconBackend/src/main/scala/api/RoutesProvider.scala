package api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import application.SurvivalComputationService
import model.BountyHuntersData

class RoutesProvider(survivalComputationService: SurvivalComputationService) {

  val routes: Route =
    cors() {
      post {
        path("compute") {
          entity(as[BountyHuntersData]) { bountyHuntersData =>
            onSuccess(survivalComputationService.computeSurvivalProbability(bountyHuntersData)) { result =>
              complete(result.toString)
            }
          }
        }
      }
    }
}
package api

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future
import application.SurvivalComputationService
import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigSyntax}
import org.scalatest.BeforeAndAfterAll

import java.io.File
import scala.util.Random


class RoutesProviderSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with BeforeAndAfterAll
  with MockFactory {

  val mockSurvivalService = mock[SurvivalComputationService]

  val survivalProbability = Random.nextDouble()

  (mockSurvivalService.computeSurvivalProbability _)
    .expects(*)
    .returns(Future.successful(survivalProbability))

  val routesUnderTest: Route = new RoutesProvider(mockSurvivalService).routes

  "The /compute route" should {

    "return the computed survival probability for a valid POST request" in {
      val jsonPayload =
        """{
          |  "countdown": 8,
          |  "bounty_hunters": [
          |    {"planet": "Hoth", "day": 6},
          |    {"planet": "Hoth", "day": 7},
          |    {"planet": "Hoth", "day": 8}
          |  ]
          |}""".stripMargin

      Post("/compute", HttpEntity(`application/json`, jsonPayload)) ~> routesUnderTest ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual survivalProbability.toString
      }
    }
  }
}

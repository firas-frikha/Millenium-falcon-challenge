package application

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import model.{BountyHuntersData, Route}
import repository.RoutesQueryService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

class SurvivalComputationActorSpec
  extends AnyWordSpec
    with BeforeAndAfterAll
    with Matchers
    with MockFactory {

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val testKit = ActorTestKit(customConfig = ConfigFactory.load("application.conf"))

  "SurvivalComputationActor" when {
    "Receiving Compute message" must {
      val bountyHuntersData = BountyHuntersData(
        countdown = Random.nextInt(),
        bounty_hunters = Seq(
          BountyHuntersData.AttackPlan(planet = Random.alphanumeric.take(12).mkString, day = Random.nextInt()),
          BountyHuntersData.AttackPlan(planet = Random.alphanumeric.take(12).mkString, day = Random.nextInt()),
        )
      )
      "RoutesQueryService returns successful response" in {

        val routesQueryServiceResponse: Seq[Route] = Seq(
          Route(origin = Random.alphanumeric.take(12).mkString,
            destination = Random.alphanumeric.take(12).mkString,
            travelTime = Random.nextInt()),
          Route(origin = Random.alphanumeric.take(12).mkString,
            destination = Random.alphanumeric.take(12).mkString,
            travelTime = Random.nextInt()))

        val routesQueryServiceMock = mock[RoutesQueryService]
        (routesQueryServiceMock.fetchAll _)
          .expects()
          .returns(Future(routesQueryServiceResponse))


        val SurvivalComputationActorBehavior = SurvivalComputationActor(
          routesQueryService = routesQueryServiceMock)

        val outputProbe = testKit.createTestProbe[SurvivalComputationActor.Output]()

        val survivalComputationActorProbe = testKit.createTestProbe[SurvivalComputationActor.Input]()
        val survivalComputationActor = testKit.spawn(Behaviors.monitor(survivalComputationActorProbe.ref, SurvivalComputationActorBehavior))

        val computeMessage = SurvivalComputationActor.Compute(bountyHuntersData = bountyHuntersData, replyTo = outputProbe.ref)

        survivalComputationActor.tell(computeMessage)

        survivalComputationActorProbe.expectMessage(computeMessage)
        survivalComputationActorProbe.expectMessage(SurvivalComputationActor.SuccessfulFetchedRoutes(routesQueryServiceResponse, bountyHuntersData, outputProbe.ref))

        outputProbe.expectMessageType[SurvivalComputationActor.SurvivalPercentage](10.seconds)
      }

      "RoutesQueryService returns exception" in {
        val exception = new RuntimeException("Unknown exception")

        val routesQueryServiceMock = mock[RoutesQueryService]
        (routesQueryServiceMock.fetchAll _)
          .expects()
          .returns(Future.failed(exception))

        val SurvivalComputationActorBehavior = SurvivalComputationActor(
          routesQueryService = routesQueryServiceMock)

        val outputProbe = testKit.createTestProbe[SurvivalComputationActor.Output]()

        val survivalComputationActorProbe = testKit.createTestProbe[SurvivalComputationActor.Input]()
        val survivalComputationActor = testKit.spawn(Behaviors.monitor(survivalComputationActorProbe.ref, SurvivalComputationActorBehavior))

        val computeMessage = SurvivalComputationActor.Compute(bountyHuntersData = bountyHuntersData, replyTo = outputProbe.ref)

        survivalComputationActor.tell(computeMessage)

        survivalComputationActorProbe.expectMessage(computeMessage)
        survivalComputationActorProbe.expectMessage(SurvivalComputationActor.FailedFetchedRoutes(exception, outputProbe.ref))

        outputProbe.expectMessage(SurvivalComputationActor.FailedSurvivalComputation(exception.getMessage))
      }
    }
  }
}

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


class SurvivalComputationActorSpec
  extends AnyWordSpec
    with BeforeAndAfterAll
    with Matchers
    with MockFactory {

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val testKit = ActorTestKit(customConfig = ConfigFactory.load("application.conf"))

  "SurvivalComputationActor" when {
    "Receiving Compute message" must {

      "RoutesQueryService returns successful response" when {
        val routesQueryServiceResponse: Seq[Route] = Seq(
          Route(origin = "Tatooine",
            destination = "Dagobah",
            travelTime = 6),
          Route(origin = "Dagobah",
            destination = "Endor",
            travelTime = 4),
          Route(origin = "Dagobah",
            destination = "Hoth",
            travelTime = 1),
          Route(origin = "Hoth",
            destination = "Endor",
            travelTime = 1),
          Route(origin = "Tatooine",
            destination = "Hoth",
            travelTime = 6)
        )
        "SurvivalPercentage should be 0" in {

          val bountyHuntersData = BountyHuntersData(
            countdown = 7,
            bounty_hunters = Seq(
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 6),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 7),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 8)
            )
          )

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

          outputProbe.expectMessage(SurvivalComputationActor.SurvivalPercentage(0))
        }

        "SurvivalPercentage should be 0.81" in {

          val bountyHuntersData = BountyHuntersData(
            countdown = 8,
            bounty_hunters = Seq(
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 6),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 7),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 8)
            )
          )

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

          outputProbe.expectMessage(SurvivalComputationActor.SurvivalPercentage(0.81))
        }

        "SurvivalPercentage should be 0.9" in {

          val bountyHuntersData = BountyHuntersData(
            countdown = 9,
            bounty_hunters = Seq(
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 6),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 7),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 8)
            )
          )

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

          outputProbe.expectMessage(SurvivalComputationActor.SurvivalPercentage(0.9))
        }

        "SurvivalPercentage should be 1" in {

          val bountyHuntersData = BountyHuntersData(
            countdown = 10,
            bounty_hunters = Seq(
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 6),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 7),
              BountyHuntersData.AttackPlan(planet = "Hoth", day = 8)
            )
          )

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

          outputProbe.expectMessage(SurvivalComputationActor.SurvivalPercentage(1))
        }

      }
      "RoutesQueryService returns exception" in {

        val bountyHuntersData = BountyHuntersData(
          countdown = 7,
          bounty_hunters = Seq(
            BountyHuntersData.AttackPlan(planet = "Hoth", day = 6),
            BountyHuntersData.AttackPlan(planet = "Hoth", day = 7),
            BountyHuntersData.AttackPlan(planet = "Hoth", day = 8)
          )
        )

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

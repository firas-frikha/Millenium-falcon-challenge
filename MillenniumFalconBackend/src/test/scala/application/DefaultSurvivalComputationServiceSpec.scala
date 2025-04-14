package application

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import model.BountyHuntersData
import model.BountyHuntersData.AttackPlan
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt
import scala.util.Random

class DefaultSurvivalComputationServiceSpec
  extends ScalaTestWithActorTestKit
    with AnyWordSpecLike
    with Matchers
    with ScalaFutures {

  "DefaultSurvivalComputationService" should {

    "return the survival percentage when the actor replies with a SurvivalPercentage" in {
      val survivalComputationActor = testKit.spawn(Behaviors.receiveMessage[SurvivalComputationActor.Input] {
        case SurvivalComputationActor.Compute(_, replyTo) =>
          replyTo ! SurvivalComputationActor.SurvivalPercentage(0.75)
          Behaviors.same
      })

      val service = new DefaultSurvivalComputationService(survivalComputationActor)(system)

      val bountyData = BountyHuntersData(Random.nextInt(),
        Seq(AttackPlan(Random.alphanumeric.take(12).mkString, Random.nextInt())))

      val futureResult = service.computeSurvivalProbability(bountyData)

      futureResult.futureValue shouldEqual 0.75
    }

    "fail with IllegalStateException when the actor replies with FailedSurvivalComputation" in {

      val exception = new Exception("Unknown exception")
      val survivalComputationActor = testKit.spawn(Behaviors.receiveMessage[SurvivalComputationActor.Input] {
        case SurvivalComputationActor.Compute(_, replyTo) =>
          replyTo ! SurvivalComputationActor.FailedSurvivalComputation(exception.getMessage)
          Behaviors.same
      })

      val service = new DefaultSurvivalComputationService(survivalComputationActor)(system)

      val bountyData = BountyHuntersData(Random.nextInt(),
        Seq(AttackPlan(Random.alphanumeric.take(12).mkString, Random.nextInt())))

      val futureResult = service.computeSurvivalProbability(bountyData)

      val thrown = futureResult.failed.futureValue
      thrown shouldBe a [IllegalStateException]
      thrown.getMessage shouldEqual exception.getMessage
    }
  }
}

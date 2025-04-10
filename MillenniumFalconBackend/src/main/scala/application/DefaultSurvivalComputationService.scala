package application

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import model.BountyHuntersData

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

class DefaultSurvivalComputationService(survivalComputationActor: ActorRef[SurvivalComputationActor.Input])
                                       (implicit system: ActorSystem[_]) extends SurvivalComputationService {

  implicit val timeout: Timeout = Timeout(5.seconds)
  implicit val executionContext: ExecutionContext = system.executionContext

  override def computeSurvivalProbability(bountyHuntersData: BountyHuntersData): Future[Double] = {
    survivalComputationActor.ask(replyTo => SurvivalComputationActor.Compute(bountyHuntersData, replyTo)).map {
      case SurvivalComputationActor.SurvivalPercentage(survivalPercentage) => survivalPercentage
      case SurvivalComputationActor.FailedSurvivalComputation(reason) => throw new IllegalStateException(reason)
    }
  }
}

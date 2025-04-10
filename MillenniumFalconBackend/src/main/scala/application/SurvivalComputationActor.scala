package application

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import model.{BountyHuntersData, Route}
import repository.RoutesQueryService

import scala.util.{Failure, Random, Success}

object SurvivalComputationActor {


  def calculator(bountyHuntersData: BountyHuntersData,
                 routes: Seq[Route],
                 replyTo: ActorRef[Output]): Behavior[Input] = Behaviors.setup { calculatorContext =>

    Behaviors.receiveMessage {
      case InitiateComputation =>

        //todo: implement here computation logic
        val survivalPercentage: Double = Random.nextDouble()
        replyTo.tell(SurvivalPercentage(survivalPercentage))

        Behaviors.same
    }
  }

  def apply(routesQueryService: RoutesQueryService): Behavior[Input] = Behaviors.setup { behaviorContext =>

    Behaviors.receiveMessage {
      case Compute(bountyHuntersData, replyTo) =>
        behaviorContext.pipeToSelf(routesQueryService.fetchAll()) {
          case Failure(exception) => FailedFetchedRoutes(exception, replyTo)
          case Success(routes) => SuccessfulFetchedRoutes(routes, bountyHuntersData, replyTo)
        }
        Behaviors.same

      case FailedFetchedRoutes(exception, replyTo) =>
        replyTo.tell(FailedSurvivalComputation(exception.getMessage))

        Behaviors.same

      case SuccessfulFetchedRoutes(routes, bountyHuntersData, replyTo) =>

        behaviorContext.self.tell(InitiateComputation)
        calculator(bountyHuntersData = bountyHuntersData, routes = routes, replyTo = replyTo)
    }
  }


  sealed trait Input

  case class Compute(bountyHuntersData: BountyHuntersData,
                     replyTo: ActorRef[Output]) extends Input

  case class SuccessfulFetchedRoutes(routes: Seq[Route], bountyHuntersData: BountyHuntersData, replyTo: ActorRef[Output]) extends Input

  case class FailedFetchedRoutes(exception: Throwable, replyTo: ActorRef[Output]) extends Input

  case object InitiateComputation extends Input

  sealed trait Output

  case class SurvivalPercentage(survivalPercentage: Double) extends Output

  case class FailedSurvivalComputation(reason: String) extends Output
}

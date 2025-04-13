package application

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import infrastructure.MillenniumFalconConfiguration
import model.{BountyHuntersData, Route}
import repository.RoutesQueryService

import scala.util.{Failure, Success}

object SurvivalComputationActor {

  def apply(routesQueryService: RoutesQueryService): Behavior[Input] = Behaviors.setup { behaviorContext =>

    val millenniumFalconConfiguration = behaviorContext.system.extension(MillenniumFalconConfiguration)

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

        behaviorContext.self.tell(InitiateComputation(bountyHuntersData, routes, replyTo))

        Behaviors.same

      case InitiateComputation(bountyHuntersData, routes, replyTo) =>

        val adjacencyList = buildAdjacencyList(routes)

        val survivalPercentage: Double = bfsTraversal(
          autonomy = millenniumFalconConfiguration.autonomy,
          startPlanet = millenniumFalconConfiguration.departure,
          targetPlanet = millenniumFalconConfiguration.arrival,
          adjacencyList = adjacencyList,
          bountyHuntersData = bountyHuntersData
        )
        replyTo.tell(SurvivalPercentage(survivalPercentage))

        Behaviors.same
    }
  }


  sealed trait Input

  case class Compute(bountyHuntersData: BountyHuntersData,
                     replyTo: ActorRef[Output]) extends Input

  case class SuccessfulFetchedRoutes(routes: Seq[Route], bountyHuntersData: BountyHuntersData, replyTo: ActorRef[Output]) extends Input

  case class FailedFetchedRoutes(exception: Throwable, replyTo: ActorRef[Output]) extends Input

  case class InitiateComputation(bountyHuntersData: BountyHuntersData, routes: Seq[Route], replyTo: ActorRef[Output]) extends Input

  sealed trait Output

  case class SurvivalPercentage(survivalPercentage: Double) extends Output

  case class FailedSurvivalComputation(reason: String) extends Output
}

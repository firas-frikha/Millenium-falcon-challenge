package entrypoint

import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import api.{Routes, Server}
import application.{DefaultSurvivalComputationService, SurvivalComputationActor, SurvivalComputationService}

final class ServerApp(context: ActorContext[_]) {

  implicit val system: ActorSystem[_] = context.system

  def start(): Behavior[Nothing] = {

    val serverActor = context.spawnAnonymous(Server())

    val survivalComputationActorRef = context.spawnAnonymous(SurvivalComputationActor(routesQueryService = ???))

    val survivalComputationService = new DefaultSurvivalComputationService(survivalComputationActorRef)
    val serverRoutes: Routes = new Routes(survivalComputationService)

    serverActor.tell(Server.Bind("localHost", 8080, serverRoutes.routes))

    Behaviors.receiveSignal[Nothing] {
      case (_, Terminated(_)) =>
        Behaviors.stopped
    }
  }
}

object ServerApp {
  def main(array: Array[String]): Unit = {

    val rootBehavior = Behaviors.setup[Nothing] { context =>
      new ServerApp(context).start()
    }

    ActorSystem[Nothing](rootBehavior, "lottery-service")
  }
}

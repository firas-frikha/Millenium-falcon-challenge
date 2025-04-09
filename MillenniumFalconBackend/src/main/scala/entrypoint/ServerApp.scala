package entrypoint

import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import api.{Routes, Server}

final class ServerApp(context: ActorContext[_]) {

  def start(): Behavior[Nothing] = {

    val serverActor = context.spawnAnonymous(Server())

    serverActor.tell(Server.Bind("localHost", 8080, Routes.routes))

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

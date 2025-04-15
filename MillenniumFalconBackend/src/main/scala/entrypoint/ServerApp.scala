package entrypoint

import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import api.{RoutesProvider, Server}
import application.{DefaultSurvivalComputationService, SurvivalComputationActor}
import com.typesafe.config.{ConfigFactory, ConfigParseOptions, ConfigSyntax}
import infrastructure.{DefaultHttpBinder, DefaultRoutesQueryService, ServerConfiguration, SlickSessionProvider}
import repository.RoutesSchema

import java.io.File

final class ServerApp(context: ActorContext[_]) {

  implicit val system: ActorSystem[_] = context.system

  def start(): Behavior[Nothing] = {
    val httpBinder = new DefaultHttpBinder()
    val serverActor = context.spawnAnonymous(Server(httpBinder))

    val slickSession = system.extension(SlickSessionProvider)
    val serverConfiguration = system.extension(ServerConfiguration)

    val defaultRoutesQueryService = new DefaultRoutesQueryService(new RoutesSchema(), slickSession.slickSession)
    val survivalComputationActorRef = context.spawnAnonymous(SurvivalComputationActor(routesQueryService = defaultRoutesQueryService))

    val survivalComputationService = new DefaultSurvivalComputationService(survivalComputationActorRef)
    val serverRoutes: RoutesProvider = new RoutesProvider(survivalComputationService)

    serverActor.tell(Server.Bind(serverConfiguration.serverAddress, serverConfiguration.serverPort, serverRoutes.routes))

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


    //todo : update file path to be read from env variable
    val defaultConfig = ConfigFactory.parseFileAnySyntax(new File("/Users/firasfrikha/projects/fullStackProjects/Millenium-falcon-challenge/MillenniumFalconBackend/src/main/resources/application.conf"))

    // todo: update file path to be read as parameter
    val millenniumFalconParams = ConfigFactory.parseFile(new File("/Users/firasfrikha/projects/fullStackProjects/Millenium-falcon-challenge/MillenniumFalconBackend/src/main/resources/millennium-falcon.json"),
      ConfigParseOptions.defaults().setSyntax(ConfigSyntax.JSON)
    )

    val appConfig = defaultConfig
      .withFallback(millenniumFalconParams)
      .resolve()

    ActorSystem[Nothing](rootBehavior, "lottery-service", appConfig)
  }
}

package entrypoint

import akka.actor.typed.{ActorSystem, Behavior, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.stream.alpakka.slick.scaladsl.SlickSession
import api.{Routes, Server}
import application.{DefaultSurvivalComputationService, SurvivalComputationActor, SurvivalComputationService}
import com.typesafe.config.{Config, ConfigFactory}
import infrastructure.DefaultRoutesQueryService
import repository.RoutesSchema
import slick.basic.DatabaseConfig
import slick.jdbc.SQLiteProfile

import java.sql.DriverManager

final class ServerApp(context: ActorContext[_]) {

  implicit val system: ActorSystem[_] = context.system

  def start(): Behavior[Nothing] = {

    val serverActor = context.spawnAnonymous(Server())


    val dbPath = "/Users/firasfrikha/projects/fullStackProjects/Millenium-falcon-challenge/MillenniumFalconBackend/src/main/resources/universe.db"

    val configStr =
      s"""
         |  profile = "slick.jdbc.SQLiteProfile$$"
         |  db {
         |    driver = "org.sqlite.JDBC"
         |    url = "jdbc:sqlite:$dbPath"
         |    connectionPool = disabled
         |    keepAliveConnection = true
         |  }
         |""".stripMargin

    //todo : update url to read it from config
    val config: Config = ConfigFactory.parseString(configStr)
      .withFallback(ConfigFactory.defaultReference()) // <-- this is key
      .resolve()

    val slickSession = SlickSession.forConfig(config)

    val defaultRoutesQueryService = new DefaultRoutesQueryService(new RoutesSchema(), slickSession)

    val survivalComputationActorRef = context.spawnAnonymous(SurvivalComputationActor(routesQueryService = defaultRoutesQueryService))

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

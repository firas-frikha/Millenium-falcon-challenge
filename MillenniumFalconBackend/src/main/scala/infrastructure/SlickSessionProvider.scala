package infrastructure

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.typesafe.config.{Config, ConfigFactory}

class SlickSessionProvider(databasePath: String) extends Extension {
  val configStr =
    s"""
       |  profile = "slick.jdbc.SQLiteProfile$$"
       |  db {
       |    driver = "org.sqlite.JDBC"
       |    url = "jdbc:sqlite:$databasePath"
       |    connectionPool = disabled
       |    keepAliveConnection = true
       |  }
       |""".stripMargin

  val databaseConfig: Config = ConfigFactory.parseString(configStr)
    .withFallback(ConfigFactory.defaultReference())
    .resolve()

  val slickSession: SlickSession = SlickSession.forConfig(databaseConfig)

}

object SlickSessionProvider extends ExtensionId[SlickSessionProvider] {

  override def createExtension(system: ActorSystem[_]): SlickSessionProvider = {
    val dataBasePAth = system.extension(MillenniumFalconConfiguration).databaseUrl

    new SlickSessionProvider(dataBasePAth)
  }

}

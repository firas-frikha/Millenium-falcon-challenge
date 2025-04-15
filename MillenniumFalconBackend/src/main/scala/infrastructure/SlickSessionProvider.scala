package infrastructure

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.typesafe.config.{Config, ConfigFactory}

import java.nio.file.Paths

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

    val dataBasePath = system.extension(MillenniumFalconConfiguration).databaseUrl
    val resolvedPath = {
      val path = Paths.get(dataBasePath)
      if (path.isAbsolute) path.toString
      else {
        val baseDir = Paths.get(System.getProperty("user.dir"))
        baseDir.resolve(path).toAbsolutePath.toString
      }
    }
    new SlickSessionProvider(resolvedPath)
  }
}

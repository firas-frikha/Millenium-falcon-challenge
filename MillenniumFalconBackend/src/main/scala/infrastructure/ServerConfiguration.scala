package infrastructure

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import com.typesafe.config.Config
import infrastructure.ServerConfiguration.{ServerAddress, ServerPort}

class ServerConfiguration(config: Config) extends Extension{

  val serverAddress = config.getString(ServerAddress)
  val serverPort = config.getInt(ServerPort)

}

object ServerConfiguration extends ExtensionId[ServerConfiguration]{
  val ServerProperty = "server"

  val ServerAddress = "address"
  val ServerPort = "port"

  override def createExtension(system: ActorSystem[_]): ServerConfiguration =
    new ServerConfiguration(system.settings.config.getConfig(ServerProperty))
}

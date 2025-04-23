package infrastructure

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import com.typesafe.config.Config
import infrastructure.MillenniumFalconConfiguration.{Arrival, Autonomy, DatabaseRoute, Departure}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

final class MillenniumFalconConfiguration(config: Config) extends Extension {
  final val autonomy = config.getInt(Autonomy)
  final val departure = config.getString(Departure)
  final val arrival = config.getString(Arrival)
  final val databaseUrl = config.getString(DatabaseRoute)
}

object MillenniumFalconConfiguration extends ExtensionId[MillenniumFalconConfiguration] {

  final case class Configuration(autonomy: Int,
                                 departure: String,
                                 arrival: String,
                                 routes_db: String)

  object Configuration {
    implicit val configurationFormat: RootJsonFormat[Configuration] = jsonFormat4(Configuration.apply)
  }

  val Autonomy = "autonomy"
  val Departure = "departure"
  val Arrival = "arrival"
  val DatabaseRoute = "routes_db"

  override def createExtension(system: ActorSystem[_]): MillenniumFalconConfiguration =
    new MillenniumFalconConfiguration(system.settings.config)
}
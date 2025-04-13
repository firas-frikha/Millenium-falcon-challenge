package infrastructure

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import com.typesafe.config.Config
import infrastructure.MillenniumFalconConfiguration.{Arrival, Autonomy, DatabaseRoute, Departure}

final class MillenniumFalconConfiguration(config: Config) extends Extension{
  final val autonomy = config.getString(Autonomy)
  final val departure = config.getString(Departure)
  final val arrival = config.getString(Arrival)
  final val databaseUrl = config.getString(DatabaseRoute)
}

object MillenniumFalconConfiguration extends ExtensionId[MillenniumFalconConfiguration] {

  val Autonomy = "autonomy"
  val Departure = "departure"
  val Arrival = "arrival"
  val DatabaseRoute = "routes_db"

  override def createExtension(system: ActorSystem[_]): MillenniumFalconConfiguration =
    new MillenniumFalconConfiguration(system.settings.config)
}
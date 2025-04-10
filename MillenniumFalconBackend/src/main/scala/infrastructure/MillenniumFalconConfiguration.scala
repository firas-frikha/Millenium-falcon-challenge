package infrastructure

import com.typesafe.config.Config
import infrastructure.MillenniumFalconConfiguration.{Arrival, Autonomy, DatabaseRoute, Departure}

final class MillenniumFalconConfiguration(config: Config) {
  final val autonomy = config.getString(Autonomy)
  final val departure = config.getString(Departure)
  final val arrival = config.getString(Arrival)
  final val databaseUrl = config.getString(DatabaseRoute)
}

object MillenniumFalconConfiguration {

  val Autonomy = "autonomy"
  val Departure = "Departure"
  val Arrival = "arrival"
  val DatabaseRoute = "routes_db"
}
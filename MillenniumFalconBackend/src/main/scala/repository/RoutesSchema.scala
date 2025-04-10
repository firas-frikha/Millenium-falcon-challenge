package repository

import model.Route
import slick.lifted.{ProvenShape, TableQuery}

class RoutesSchema {

  lazy val routesTable = TableQuery[RoutesTable]

  import slick.jdbc.SQLiteProfile.api._

  class RoutesTable(tag: Tag) extends Table[Route](tag, "routes") {

    def origin: Rep[String] = column[String]("origin")

    def destination: Rep[String] = column[String]("destination")

    def travelTime: Rep[Int] = column[Int]("travel_time")

    override def * : ProvenShape[Route] = (origin, destination, travelTime) <> (Route.tupled, Route.unapply)
  }

}

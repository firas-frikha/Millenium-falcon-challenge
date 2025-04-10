package infrastructure

import akka.stream.alpakka.slick.scaladsl.SlickSession
import model.Route
import repository.{RoutesQueryService, RoutesSchema}

import scala.concurrent.Future

class DefaultRoutesQueryService(routesSchema: RoutesSchema,
                                slickSession: SlickSession) extends RoutesQueryService {

  import slick.jdbc.SQLiteProfile.api._

  override def fetchAll(): Future[Seq[Route]] =
    slickSession.db.run(routesSchema.routesTable.result)
}

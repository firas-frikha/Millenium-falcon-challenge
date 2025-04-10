package repository

import model.Route

import scala.concurrent.Future

trait RoutesQueryService {
  def fetchAll(): Future[Seq[Route]]
}

package application

import model.BountyHuntersData

import scala.concurrent.Future

trait SurvivalComputationService {
  def computeSurvivalProbability(bountyHuntersData: BountyHuntersData): Future[Double]
}

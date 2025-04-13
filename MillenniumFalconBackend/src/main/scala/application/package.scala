import model.{BfsState, BountyHuntersData, Route}

import scala.collection.mutable

package object application {
  def buildAdjacencyList(routes: Seq[Route]): Map[String, Seq[(String, Int)]] =
    routes.flatMap { route =>
        Seq((route.origin, (route.destination, route.travelTime)),
          (route.destination, (route.origin, route.travelTime)))
      }.groupBy(_._1)
      .map {
        case (vertex, tuples) => (vertex, tuples.map(_._2))
      }


  def bfsTraversal(autonomy: Int, startPlanet: String, targetPlanet: String, adjacencyList: Map[String, Seq[(String, Int)]], bountyHuntersData: BountyHuntersData): Double = {
    val initialStartingPlanet = BfsState(
      planet = startPlanet,
      currentDay = 0,
      autonomy = autonomy,
      survivalProbability = 1)

    val queue: mutable.Queue[BfsState] = mutable.Queue(initialStartingPlanet)

    var bestProbability = 0.0

    while (queue.nonEmpty) {
      val current = queue.dequeue()

      if (current.currentDay > bountyHuntersData.countdown) {}
      else {
        if (current.planet == targetPlanet) {
          bestProbability = math.max(bestProbability, current.survivalProbability)
        }

        adjacencyList.getOrElse(current.planet, Seq.empty).foreach {
          case (nextNeighbor, distance) =>
            if (current.autonomy >= distance) {
              val updatedArrivalDate = current.currentDay + distance
              val newSurvivalProbability =
                if (bountyHuntersData.bounty_hunters.map(attackPlan => (attackPlan.planet, attackPlan.day)).contains((nextNeighbor, updatedArrivalDate)))
                  current.survivalProbability * 0.9
                else
                  current.survivalProbability

              val nextState = BfsState(
                planet = nextNeighbor,
                currentDay = updatedArrivalDate,
                autonomy = current.autonomy - distance,
                survivalProbability = newSurvivalProbability)

              queue.enqueue(nextState)
            }
        }

        val updatedDate = current.currentDay + 1
        if (updatedDate < bountyHuntersData.countdown) {
          val newSurvivalProbability =
            if (bountyHuntersData.bounty_hunters.map(attackPlan => (attackPlan.planet, attackPlan.day)).contains((current.planet, updatedDate)))
              current.survivalProbability * 0.9
            else
              current.survivalProbability

        val newState = BfsState(
          planet = current.planet,
          currentDay = updatedDate,
          autonomy = autonomy,
          survivalProbability = newSurvivalProbability
        )

        queue.enqueue(newState)
        }
      }
    }
    bestProbability
  }
}

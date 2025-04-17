package cli

import application.{bfsTraversal, buildAdjacencyList}
import infrastructure.{DefaultRoutesQueryService, MillenniumFalconConfiguration, SlickSessionProvider}
import model.BountyHuntersData
import repository.RoutesSchema

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object GiveMeTheOdds extends App {
  if (args.length != 2) {
    Console.err.print("Usage: give-me-the-odds <millennium-falcon.json> <empire.json>")
    sys.exit()
  }
  else {
    val maybeFalconConfig = readJson[MillenniumFalconConfiguration](args(0))
    val maybeBountyHuntersData = readJson[BountyHuntersData](args(1))

    (maybeFalconConfig, maybeBountyHuntersData) match {
      case (Success(falconConfig), Success(bountyHuntersData)) =>

        val slickSessionProvider = new SlickSessionProvider(falconConfig.databaseUrl)
        val defaultRoutesQueryService = new DefaultRoutesQueryService(new RoutesSchema(), slickSessionProvider.slickSession)

        val allRoutes = defaultRoutesQueryService.fetchAll()
        val result = allRoutes.map { routes =>
          val adjacencyList = buildAdjacencyList(routes)
          val survivalPercentage = bfsTraversal(falconConfig.autonomy, falconConfig.departure, falconConfig.arrival,
            adjacencyList, bountyHuntersData)
          survivalPercentage
        }
        val percentage = Await.result(result, 20.seconds)
        println(percentage * 100)

      case (Failure(err), _) =>
        Console.err.println(s"Failed to read '${args(0)}': ${err.getMessage}")
        sys.exit(2)

      case (_, Failure(err)) =>
        Console.err.println(s"Failed to read '${args(1)}': ${err.getMessage}")
        sys.exit(3)
    }

  }
}

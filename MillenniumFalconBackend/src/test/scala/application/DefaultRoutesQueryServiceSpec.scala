package application

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.typesafe.config.ConfigFactory
import infrastructure.DefaultRoutesQueryService
import model.Route
import org.scalamock.scalatest.MockFactory
import org.scalatest.Outcome
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.FixtureAnyWordSpec
import org.slf4j.LoggerFactory
import repository.RoutesSchema

import java.util.UUID
import scala.util.Random

class DefaultRoutesQueryServiceSpec
  extends FixtureAnyWordSpec
    with ScalaFutures
    with Matchers
    with MockFactory {

  override type FixtureParam = TestFixture

  final class TestFixture(val actorTestKit: ActorTestKit,
                          val slickSession: SlickSession,
                          val routesSchema: RoutesSchema)

  final val Logger = LoggerFactory.getLogger(getClass)

  override def withFixture(test: OneArgTest): Outcome = {
    val config =
      ConfigFactory.parseString(s"testDatabaseName = test-${UUID.randomUUID().toString}")
        .withFallback(ConfigFactory.parseResourcesAnySyntax("application-test-query.conf"))
        .resolve()

    val actorTestKit = ActorTestKit(
      s"{ActorTestKitBase.testNameFromCallStack()}-${UUID.randomUUID().toString}",
      config
    )

    val slickSession = SlickSession.forConfig("slick", config)
    val routesSchema = new RoutesSchema()
    import slickSession.profile.api._

    try {
      val operations = DBIO.seq(
        routesSchema.routesTable.schema.create,
      )
      whenReady(slickSession.db.run(operations), timeout(Span(6, Seconds)), interval(Span(2, Seconds))) { _ =>
        Logger.info("routes schema table has been created.")
      }
      withFixture(test.toNoArgTest(new TestFixture(actorTestKit, slickSession, routesSchema)))
    } finally {
      val operations = DBIO.seq(
        routesSchema.routesTable.schema.dropIfExists,

      )
      whenReady(slickSession.db.run(operations), timeout(Span(6, Seconds))) { _ =>
        Logger.info("routes schema table has been dropped.")
      }
      actorTestKit.shutdownTestKit()
    }
  }

  "DefaultQueryService" when {
    "fetching all routes" must {
      val routes = Seq(
        Route(
          origin = Random.alphanumeric.take(12).mkString,
          destination = Random.alphanumeric.take(12).mkString,
          travelTime = Random.nextInt()
        ),
        Route(
          origin = Random.alphanumeric.take(12).mkString,
          destination = Random.alphanumeric.take(12).mkString,
          travelTime = Random.nextInt()
        )
      )

      "return valid result" in { testFixture =>
        import testFixture.slickSession.profile.api._
        import testFixture.{routesSchema, slickSession}

        val operations = DBIO.seq(
          routesSchema.routesTable ++= routes
        )
        whenReady(slickSession.db.run(operations.transactionally), timeout(Span(5, Seconds))) { _ =>

          val queryService = new DefaultRoutesQueryService(routesSchema, slickSession)

          queryService.fetchAll()
            .futureValue(timeout(Span(5, Seconds)))
            .sortBy(_.origin)
            .mustBe(routes.sortBy(_.origin))
        }
      }
    }
  }
}
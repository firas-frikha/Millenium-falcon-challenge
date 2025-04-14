package api

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.HttpConnectionTerminated
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.net.InetSocketAddress
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class ServerSpec
  extends AnyWordSpec
    with BeforeAndAfterAll
    with Matchers
    with MockFactory {

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val testKit = ActorTestKit()

  "HttpServer" when {
    "Receiving Bind message" must {
      "Succeed to bind server" when {
        "httpBinder Succeed" in {
          val host = Random.alphanumeric.take(12).mkString
          val port = Random.nextInt(10)
          val route = mock[HttpRequest => Future[HttpResponse]]

          val binding = Http.ServerBinding(new InetSocketAddress(host, port))(() => Future.successful(()), _ => Future.successful(HttpConnectionTerminated))

          val httpBinderMock = mock[HttpBinder]
          (httpBinderMock.bind(_: String, _: Int, _: HttpRequest => Future[HttpResponse]))
            .expects(*, *, *)
            .returns(Future(binding))


          val httpServerBehavior = Server(
            httpBinder = httpBinderMock)


          val httpServerProbe = testKit.createTestProbe[Server.Input]()
          val httpServerActor = testKit.spawn(Behaviors.monitor(httpServerProbe.ref, httpServerBehavior))

          val bindMessage = Server.Bind(address = host, port = port, routes = route)

          httpServerActor.tell(bindMessage)

          httpServerProbe.expectMessage(bindMessage)

          httpServerProbe.expectMessageType[Server.LogSuccessfulServerBinding]
        }
      }

      "Fails to bind server" when {
        "httpBinder Fail" in {
          val host = Random.alphanumeric.take(12).mkString
          val port = Random.nextInt(10)
          val route = mock[HttpRequest => Future[HttpResponse]]

          val exception = new RuntimeException("Unknown exception")

          val httpBinderMock = mock[HttpBinder]
          (httpBinderMock.bind(_: String, _: Int, _: HttpRequest => Future[HttpResponse]))
            .expects(*, *, *)
            .returns(Future.failed(exception))

          val httpServerBehavior = Server(
            httpBinder = httpBinderMock)


          val httpServerProbe = testKit.createTestProbe[Server.Input]()
          val httpServerActor = testKit.spawn(Behaviors.monitor(httpServerProbe.ref, httpServerBehavior))

          val bindMessage = Server.Bind(address = host, port = port, routes = route)

          httpServerActor.tell(bindMessage)

          httpServerProbe.expectMessage(bindMessage)
          httpServerProbe.expectMessageType[Server.LogFailedServerBinding]

        }
      }
    }
  }
}

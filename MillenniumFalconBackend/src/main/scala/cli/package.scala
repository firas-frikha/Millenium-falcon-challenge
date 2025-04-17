import java.nio.file.{Files, Paths}
import io.circe.parser.decode
import io.circe.generic.auto._

import scala.util.{Failure, Success, Try}

package object cli {

  def readJson[T](path: String): Try[T] = {
    val bytes = Files.readAllBytes(Paths.get(path))
    val jsonString = new String(bytes, "UTF-8")
    decode[T](jsonString) match {
      case Left(error) => Failure(new RuntimeException(s"Failed to decode JSON: $error"))
      case Right(decodedObject) => Success(decodedObject)
    }
  }
}

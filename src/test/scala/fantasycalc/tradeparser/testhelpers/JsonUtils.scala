package fantasycalc.tradeparser.testhelpers

import java.io.File
import java.nio.file.{Files, Paths}

import io.circe.Decoder
import io.circe.parser.decode

object JsonUtils {

  def getFiles(dir: String): List[File] = {
    val directory = new File(dir)
    directory.listFiles.filter(_.isFile).toList
  }

  def load[T: Decoder](fileName: String): T = {
    val jsonString: String =
      Files.readString(Paths.get(s"src/test/resources/api-responses/$fileName"))
    (decode[T](jsonString) match {
      case Right(r) => Right(r)
      case Left(err) => println(err)
        Left(err)
    }).toOption.getOrElse {
      println(s"Unable to load file $fileName")
      throw new RuntimeException(s"Unable to load file $fileName")
    }
  }

}

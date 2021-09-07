package fantasycalc.tradeparser
//import io.circe.parser._
import java.nio.file.{Files, Path, Paths}
import java.util.stream

import fantasycalc.tradeparser.models.api.mfl.LeagueSearchApiResponse
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import scala.collection.JavaConverters._


object ApiResponses {

  object Mfl {
    val SearchLeaguesResponse: LeagueSearchApiResponse = load[LeagueSearchApiResponse]("mfl/SearchLeaguesResponse.json")
  }

  private def load[T: Decoder](path: String): T = {
    val jsonString: String = Files.readString(Paths.get(s"src/test/resources/api-responses/$path"))
    decode[T](jsonString).toOption.get
  }
}

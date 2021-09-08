package fantasycalc.tradeparser
import java.nio.file.{Files, Paths}

import fantasycalc.tradeparser.models.api.mfl.{
  LeagueSearchApiResponse,
  PlayersApiResponse
}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._

object ApiResponses {

  object Mfl {
    val SearchLeaguesResponse: LeagueSearchApiResponse =
      load[LeagueSearchApiResponse]("mfl/SearchLeaguesResponse.json")

    val PlayersResponse: PlayersApiResponse =
      load[PlayersApiResponse]("mfl/PlayersResponse.json")
  }

  private def load[T: Decoder](path: String): T = {
    val jsonString: String =
      Files.readString(Paths.get(s"src/test/resources/api-responses/$path"))
    decode[T](jsonString).toOption.get
  }
}

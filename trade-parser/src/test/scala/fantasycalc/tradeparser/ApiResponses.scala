package fantasycalc.tradeparser
import java.nio.file.{Files, Paths}

import fantasycalc.tradeparser.models.api.mfl.{LeagueApiResponse, LeagueSearchApiResponse, PlayersApiResponse, RulesApiResponse, TradesApiResponse}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._

object ApiResponses {

  object Mfl {
    val SearchLeaguesResponse: LeagueSearchApiResponse =
      load[LeagueSearchApiResponse]("mfl/SearchLeaguesResponse.json")

    val PlayersResponse: PlayersApiResponse =
      load[PlayersApiResponse]("mfl/PlayersResponse.json")

    val TradesResponse: TradesApiResponse =
      load[TradesApiResponse]("mfl/TradesResponse.json")

    val LeagueResponse: LeagueApiResponse =
      load[LeagueApiResponse]("mfl/LeagueResponse.json")

    val RulesResponse: RulesApiResponse =
      load[RulesApiResponse]("mfl/RulesResponse.json")
  }

  private def load[T: Decoder](path: String): T = {
    val jsonString: String =
      Files.readString(Paths.get(s"src/test/resources/api-responses/$path"))
    decode[T](jsonString).toOption.getOrElse {
      println(s"Unable to load file $path")
      throw new RuntimeException(s"Unable to load file $path")
    }
  }
}

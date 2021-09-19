package fantasycalc.tradeparser
import fantasycalc.tradeparser.models.api.mfl._
import fantasycalc.tradeparser.testhelpers.JsonUtils.load
import io.circe.generic.auto._

object ApiResponses {

  object Mfl {
    val SearchLeaguesResponse: LeagueSearchApiResponse =
      load[LeagueSearchApiResponse]("mfl/SearchLeaguesResponse.json")

    val PlayersResponse: PlayersApiResponse =
      load[PlayersApiResponse]("mfl/PlayersResponse.json")

    val TradesResponse: TradesApiResponse =
      load[TradesApiResponse]("mfl/trades/TradesResponse.json")

    val LeagueResponse: LeagueApiResponse =
      load[LeagueApiResponse]("mfl/leagues/LeagueResponse.json")

    val RulesResponse: RulesApiResponse =
      load[RulesApiResponse]("mfl/rules/RulesResponse.json")
  }
}

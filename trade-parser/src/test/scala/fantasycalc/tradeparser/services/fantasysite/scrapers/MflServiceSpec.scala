package fantasycalc.tradeparser.services.fantasysite.scrapers
import java.time.Instant

import cats.Id
import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.mocks.PlayerIdConverterMock
import fantasycalc.tradeparser.models.{
  FantasycalcAssetId,
  LeagueId,
  LeagueSettings,
  Starters,
  Trade
}
import fantasycalc.tradeparser.models.api.mfl.{
  LeagueApiResponse,
  LeagueSearchApiResponse,
  PlayersApiResponse,
  Points,
  PositionRules,
  Rule,
  RulesApiResponse,
  TradesApiResponse
}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MflServiceSpec extends AnyFunSpec with Matchers {
  describe("getLeagues") {
    it("should parse leagueId from response") {
      val mflService: MflService[Id] =
        new MflService[Id](MockMflClient, new PlayerIdConverterMock)
      val actual: Id[List[LeagueId]] = mflService.getLeagues
      actual.head shouldBe LeagueId("10005")
      actual.length shouldBe 4
    }
  }

  describe("getTrades") {
    it("should parse trades and FantasycalcAssetId from transaction list") {
      val mflService: MflService[Id] =
        new MflService[Id](MockMflClient, new PlayerIdConverterMock)
      val leagueId = LeagueId(123.toString)
      val actual: Id[List[Trade]] = mflService.getTrades(leagueId)

      actual.head shouldBe Trade(
        leagueId,
        Instant.parse("2021-09-01T22:14:35.000Z"),
        List(FantasycalcAssetId(15287)),
        List(FantasycalcAssetId(14141), FantasycalcAssetId(1020223))
      )
      actual.length shouldBe 10
    }
  }

  describe("getSettings") {
    it("should parse league settings") {
      val mflService =
        new MflService[Id](MockMflClient, new PlayerIdConverterMock)

      val leagueId = LeagueId(123.toString)
      val actual = mflService.getSettings(leagueId)

      actual shouldBe LeagueSettings(
        leagueId = leagueId,
        numTeams = 12,
        starters = Starters(2, 2, 4, 2),
        ppr = 1,
        isDynasty = true
      )
    }

    it("should parse half PPR") {
      val mflService =
        new MflService[Id](MockMflClient, new PlayerIdConverterMock)

      val rules = PositionRules(
        positions = "QB|WR",
        rule = List(
          Rule(
            points = Points(".5"),
            range = Points("any"),
            event = Points("CC")
          )
        )
      )
      val actual = mflService.parsePprRules(List(rules))
      actual shouldBe BigDecimal(.5)
    }
  }
}

object MockMflClient extends MflClient[Id] {
  override def searchLeagues(search: String): Id[LeagueSearchApiResponse] =
    ApiResponses.Mfl.SearchLeaguesResponse

  override def getPlayers: Id[PlayersApiResponse] =
    ApiResponses.Mfl.PlayersResponse

  override def getTrades(leagueId: LeagueId): Id[TradesApiResponse] =
    ApiResponses.Mfl.TradesResponse

  override def getRules(leagueId: LeagueId): Id[RulesApiResponse] =
    ApiResponses.Mfl.RulesResponse

  override def getLeague(leagueId: LeagueId): Id[LeagueApiResponse] =
    ApiResponses.Mfl.LeagueResponse
}

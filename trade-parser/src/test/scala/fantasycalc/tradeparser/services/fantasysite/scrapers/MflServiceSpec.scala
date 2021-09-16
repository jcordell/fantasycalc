package fantasycalc.tradeparser.services.fantasysite.scrapers
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
  RulesApiResponse,
  TradesApiResponse
}
import org.joda.time.Instant
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MflServiceSpec extends AnyFunSpec with Matchers {
  describe("getLeagues") {
    it("should parse leagueId from response") {
      val mockMflClient = new MflClient[Id] {
        override def searchLeagues(
          search: String
        ): Id[LeagueSearchApiResponse] = ApiResponses.Mfl.SearchLeaguesResponse

        override def getPlayers: Id[PlayersApiResponse] = ???

        override def getTrades(leagueId: LeagueId): Id[TradesApiResponse] = ???

        override def getRules(leagueId: LeagueId): Id[RulesApiResponse] = ???

        override def getLeague(leagueId: LeagueId): Id[LeagueApiResponse] = ???
      }

      val mflService: MflService[Id] =
        new MflService[Id](mockMflClient, new PlayerIdConverterMock)
      val actual: Id[List[LeagueId]] = mflService.getLeagues
      actual.head shouldBe LeagueId("10005")
      actual.length shouldBe 4
    }
  }

  describe("getTrades") {
    it("should parse trades and FantasycalcAssetId from transaction list") {
      val mockMflClient = new MflClient[Id] {
        override def searchLeagues(
          search: String
        ): Id[LeagueSearchApiResponse] = ???

        override def getPlayers: Id[PlayersApiResponse] = ???

        override def getTrades(leagueId: LeagueId): Id[TradesApiResponse] =
          ApiResponses.Mfl.TradesResponse

        override def getRules(leagueId: LeagueId): Id[RulesApiResponse] = ???

        override def getLeague(leagueId: LeagueId): Id[LeagueApiResponse] = ???
      }

      val mflService: MflService[Id] =
        new MflService[Id](mockMflClient, new PlayerIdConverterMock)
      val leagueId = LeagueId(123.toString)
      val actual: Id[List[Trade]] = mflService.getTrades(leagueId)

      actual.head shouldBe Trade(
        leagueId,
        Instant.parse("2021-09-01T22:14:35.000Z"),
        List(FantasycalcAssetId("15287")),
        List(FantasycalcAssetId("14141"), FantasycalcAssetId("FP_0010_2022_3"))
      )
      actual.length shouldBe 10
    }
  }

  describe("getSettings") {
    // TODO: Number of starter rules and PPR parsing isn't working yet
    it("should parse league settings") {
      val mockMflClient = new MflClient[Id] {
        override def searchLeagues(
          search: String
        ): Id[LeagueSearchApiResponse] = ???

        override def getPlayers: Id[PlayersApiResponse] = ???

        override def getTrades(leagueId: LeagueId): Id[TradesApiResponse] = ???

        override def getRules(leagueId: LeagueId): Id[RulesApiResponse] =
          ApiResponses.Mfl.RulesResponse

        override def getLeague(leagueId: LeagueId): Id[LeagueApiResponse] =
          ApiResponses.Mfl.LeagueResponse
      }

      val mflService =
        new MflService[Id](mockMflClient, new PlayerIdConverterMock)

      val leagueId = LeagueId(123.toString)
      val actual = mflService.getSettings(leagueId)

      actual shouldBe LeagueSettings(
        leagueId = leagueId,
        numTeams = 12,
        starters = Starters(1, 2, 2, 1),
        ppr = 1,
        isDynasty = true
      )
    }
  }
}

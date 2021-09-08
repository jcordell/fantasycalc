package fantasycalc.tradeparser.services.fantasysite.scrapers
import cats.Id
import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.mocks.PlayerIdConverterMock
import fantasycalc.tradeparser.models.{FantasycalcAssetId, LeagueId, Trade}
import fantasycalc.tradeparser.models.api.mfl.{LeagueSearchApiResponse, PlayersApiResponse, TradesApiResponse}
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
}

package fantasycalc.tradeparser.services.fantasysite.scrapers
import cats.Id
import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.models.api.mfl.{LeagueSearchApiResponse, PlayersApiResponse}
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
      }

      val mflService: MflService[Id] = new MflService[Id](mockMflClient)
      val actual: Id[List[LeagueId]] = mflService.getLeagues
      actual.head shouldBe LeagueId("10005")
      actual.length shouldBe 4
    }
  }

}

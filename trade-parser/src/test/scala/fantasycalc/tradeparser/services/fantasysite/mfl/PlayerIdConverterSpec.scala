package fantasycalc.tradeparser.services.fantasysite.mfl

import cats.Id
import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models.FantasycalcAssetId
import fantasycalc.tradeparser.models.api.mfl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PlayerIdConverterSpec extends AnyFunSpec with Matchers {
  describe("toFantasycalcAssetId") {
    it("should create map") {
      val mflClient = new MflClient[Id] {
        override def searchLeagues(
          search: String
        ): Id[LeagueSearchApiResponse] = ???

        override def getPlayers: Id[PlayersApiResponse] = {
          val response = ApiResponses.Mfl.PlayersResponse
          println(response)
          response
        }
      }

      val playerIdConverter = new PlayerIdConverter[Id](mflClient)
      val actual = playerIdConverter.toFantasycalcAssetId(
        MflPlayer(
          position = Some("RB"),
          name = Some(" Henry Derrick"),
          id = "12626",
          team = Some("TEN")
        )
      )
      actual shouldBe Some(FantasycalcAssetId("12626"))

    }
  }

}

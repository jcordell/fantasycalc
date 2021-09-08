package fantasycalc.tradeparser.services.fantasysite.mfl

import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.models.FantasycalcAssetId
import fantasycalc.tradeparser.models.api.mfl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PlayerIdConverterSpec extends AnyFunSpec with Matchers {
  describe("toFantasycalcAssetId") {
    it("should correctly remap ids") {
      val playerIdConverter =
        new PlayerIdConverter(ApiResponses.Mfl.PlayersResponse)
      val actual = playerIdConverter.toFantasycalcAssetId(
        MflId("12626")
      )
      actual shouldBe Some(FantasycalcAssetId("12626"))

    }
  }

}

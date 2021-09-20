package fantasycalc.tradeparser.services.fantasysite.mfl

import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.models.{
  FantasycalcAssetId,
  Player,
  PlayerName,
  Position
}
import fantasycalc.tradeparser.models.api.mfl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PlayerIdConverterSpec extends AnyFunSpec with Matchers {
  describe("toFantasycalcAssetId") {
    it("should correctly remap ids") {
      val playerIdConverter =
        new PlayerIdConverter(
          List(
            Player(
              id = FantasycalcAssetId(100),
              name = PlayerName("Player 1"),
              mflId = MflId("1"),
              position = Position.WR
            )
          )
        )

      val actual = playerIdConverter.toFantasycalcAssetId(MflId("1"))
      actual shouldBe Some(FantasycalcAssetId(100))

    }

    it("should remap future picks") {
      val playerIdConverter =
        new PlayerIdConverter(
          List(
            Player(
              id = FantasycalcAssetId(100),
              name = PlayerName("Player 1"),
              mflId = MflId("FP_2022_2"),
              position = Position.WR
            )
          )
        )

      val actual = playerIdConverter.toFantasycalcAssetId(MflId("FP_0001_2022_2"))
      actual shouldBe Some(FantasycalcAssetId(100))

    }
  }

}

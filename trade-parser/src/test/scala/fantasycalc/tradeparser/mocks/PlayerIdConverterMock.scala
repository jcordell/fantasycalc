package fantasycalc.tradeparser.mocks

import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl.MflId
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter

class PlayerIdConverterMock extends PlayerIdConverter(List.empty) {

  override def toFantasycalcAssetId(mflId: MflId): Option[FantasycalcAssetId] =
    // removing non ints so picks can be converted
    mflId.id.replaceAll("[^\\d.]", "").toIntOption.map(FantasycalcAssetId.apply)
}

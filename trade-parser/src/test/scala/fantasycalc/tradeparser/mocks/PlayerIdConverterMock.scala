package fantasycalc.tradeparser.mocks

import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl.MflId
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter

class PlayerIdConverterMock extends PlayerIdConverter(List.empty) {

  override def toFantasycalcAssetId(mflId: MflId): Option[FantasycalcAssetId] =
    Some(FantasycalcAssetId(mflId.id))
}

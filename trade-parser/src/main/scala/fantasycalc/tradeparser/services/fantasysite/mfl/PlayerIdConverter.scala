package fantasycalc.tradeparser.services.fantasysite.mfl

import fantasycalc.tradeparser.models.api.mfl.MflId
import fantasycalc.tradeparser.models.{FantasycalcAssetId, Player}

class PlayerIdConverter(playersApiResponse: List[Player]) {
  def toFantasycalcAssetId(mflId: MflId): Option[FantasycalcAssetId] =
    mflIdToInternalIdMap.get(mflId)

  private val mflIdToInternalIdMap: Map[MflId, FantasycalcAssetId] =
    playersApiResponse.map(player => player.mflId -> player.id).toMap
}

package fantasycalc.tradeparser.services.fantasysite.mfl

import fantasycalc.tradeparser.models.FantasycalcAssetId
import fantasycalc.tradeparser.models.api.mfl.{MflId, PlayersApiResponse}

// TODO: Should this be a `AssetId` typeclass?
// TODO: This will take something other than PlayersApiResponse once using an actual internal id map.
class PlayerIdConverter(playersApiResponse: PlayersApiResponse) {
  // TODO: Actually create/use internal FantasycalcAssetId.
  def toFantasycalcAssetId(mflId: MflId): Option[FantasycalcAssetId] =
    Some(FantasycalcAssetId(mflId.id))

}

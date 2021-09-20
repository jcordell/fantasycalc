package fantasycalc.tradeparser.services.fantasysite.mfl

import fantasycalc.tradeparser.models.api.mfl.MflId
import fantasycalc.tradeparser.models.{FantasycalcAssetId, Player}

import scala.util.Try

class PlayerIdConverter(playersApiResponse: List[Player]) {
  def toFantasycalcAssetId(mflId: MflId): Option[FantasycalcAssetId] =
    mflIdToInternalIdMap
      .get(mflId)
      .orElse {
        // TODO (low priority): One to many playerId -> MFL database relationship
        // MFL future pick ids are format FP_<FRANCHISE_ID>_<YEAR>_<ROUND>
        // Database can't have duplicate player ids so remove franchise from pick id.
        val reformattedPick = Try(mflId.id.replace(mflId.id.substring(2, 7), ""))
        reformattedPick.toOption.flatMap(formattedPick => mflIdToInternalIdMap.get(MflId(formattedPick)))
      }
      .orElse {
        println(
          s"Unable to convert mflId=$mflId to internal FantasycalcAssetId"
        )
        None
      }

  private val mflIdToInternalIdMap: Map[MflId, FantasycalcAssetId] =
    playersApiResponse.map(player => player.mflId -> player.id).toMap
}

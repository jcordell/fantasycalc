package fantasycalc.tradeparser.services.fantasysite.services

import fantasycalc.tradeparser.models.{FantasycalcAssetId, Player, PlayerName}
import fantasycalc.tradeparser.models.api.mfl.PlayersApiResponse

class PlayerInfoService(playersApiResponse: PlayersApiResponse) {

  def getPlayerInfo: FantasycalcAssetId => Option[Player] = lookupMap.get

  private val lookupMap: Map[FantasycalcAssetId, Player] =
    playersApiResponse.players.player
      .map(
        player =>
          FantasycalcAssetId(player.id) -> Player(
            FantasycalcAssetId(player.id),
            PlayerName(player.name.getOrElse("Unknown"))
          )
      )
      .toMap

}

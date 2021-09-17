package fantasycalc.tradeparser.services.fantasysite.services

import fantasycalc.tradeparser.models.{FantasycalcAssetId, Player, PlayerName, Position}
import fantasycalc.tradeparser.models.api.mfl.{MflId, PlayersApiResponse}

class PlayerInfoService(playersApiResponse: PlayersApiResponse) {

  def getPlayerInfo: FantasycalcAssetId => Option[Player] = lookupMap.get

  // TODO: Use the list of players from the database to make this service
  private val lookupMap: Map[FantasycalcAssetId, Player] =
    playersApiResponse.players.player
      .map(
        player =>
          FantasycalcAssetId(player.id.getOrElse("Unknown")) -> Player(
            FantasycalcAssetId(player.id.getOrElse("Unknown")),
            PlayerName(player.name.getOrElse("Unknown")),
            MflId("123"),
            Position.WR
          )
      )
      .toMap

}

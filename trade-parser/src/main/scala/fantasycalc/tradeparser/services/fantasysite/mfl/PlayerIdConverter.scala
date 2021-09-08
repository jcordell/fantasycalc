package fantasycalc.tradeparser.services.fantasysite.mfl

import cats.Monad
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models.FantasycalcAssetId
import fantasycalc.tradeparser.models.api.mfl.MflPlayer
import cats.implicits._

class PlayerIdConverter[F[_]: Monad](mflClient: MflClient[F]) {
  def toFantasycalcAssetId(
    mflPlayer: MflPlayer
  ): F[Option[FantasycalcAssetId]] = getPlayerIdLookup.mapApply(mflPlayer)

  private def getPlayerIdLookup: F[MflPlayer => Option[FantasycalcAssetId]] =
    for {
      playerResponse <- mflClient.getPlayers
    } yield {
      // TODO: Actually create/use internal FantasycalcAssetId.
      val lookupMap: Map[MflPlayer, FantasycalcAssetId] =
        playerResponse.players.player
          .map(player => player -> FantasycalcAssetId(player.id))
          .toMap
      lookupMap.get _
    }

}

package fantasycalc.tradeparser.services.fantasysite.scrapers

import cats.Monad
import cats.implicits._
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl.MflId
import fantasycalc.tradeparser.services.fantasysite.FantasySiteService
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter
import org.joda.time.Instant

class MflService[F[_]: Monad](mflClient: MflClient[F],
                              playerIdConverter: PlayerIdConverter)
    extends FantasySiteService[F, FantasySite.MFL.type] {

  // TODO: Search term should be 'a' to 'z' to fetch all leagues
  override def getLeagues: F[List[LeagueId]] =
    for {
      leaguesResponse <- mflClient.searchLeagues("dynasty")
    } yield leaguesResponse.leagues.league.map(league => LeagueId(league.id))

  override def getTrades(leagueId: LeagueId): F[List[Trade]] =
    for {
      trades <- mflClient.getTrades(leagueId)
    } yield {
      trades.transactions.transaction.map(trade => {
        Trade(
          leagueId,
          Instant.ofEpochSecond(trade.timestamp.toInt),
          toTradeList(trade.franchise1_gave_up),
          toTradeList(trade.franchise2_gave_up)
        )
      })
    }

  private def toTradeList(tradeString: String): List[FantasycalcAssetId] = {
    tradeString
      .split(",")
      .toList
      .map(MflId.apply)
      .flatMap(playerIdConverter.toFantasycalcAssetId)
  }

  override def getSettings(leagueId: LeagueId): F[List[LeagueSettings]] = ???
}

package fantasycalc.tradeparser.services.fantasysite

import cats.Monad
import cats.effect.Concurrent
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream

class FantasySiteUpdateService[F[_]: Monad: Concurrent](
  site: FantasySiteService[F],
  topics: Topics[F],
  databaseService: DatabaseService[F]
) {

  def stream: Stream[F, Any] =
    Stream(streamTrades, streamLeagueSettings, streamLeagueIds).parJoin(3)

  def streamLeagueIds: Stream[F, LeagueId] =
    Stream
      .evalSeq(site.getLeagues)
      .evalTap(databaseService.storeLeagueId)
      .evalTap(topics.leagueId.publish1)

  def streamTrades: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMap(site.getTrades)
      .flatMap(fs2.Stream.apply(_: _*))
      .evalMap(databaseService.storeTrade)

  def streamLeagueSettings: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMap(site.getSettings)
      .evalMap(databaseService.storeLeagueSettings)
}

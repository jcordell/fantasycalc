package fantasycalc.tradeparser.services.fantasysite

import cats.Monad
import cats.effect.Concurrent
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream

class FantasySiteUpdateService[F[_]: Monad: Concurrent, T <: FantasySite](
  site: FantasySiteService[F, T],
  topics: Topics[F],
  databaseService: DatabaseService[F]
) {

  def stream: Stream[F, Int] =
    streamLeagueIds
      .merge(streamTrades)
      .merge(streamLeagueSettings)

  protected def streamLeagueIds: Stream[F, Nothing] =
    Stream
      .evalSeq(site.getLeagues)
      .evalTap(databaseService.storeLeagueId)
      .through(topics.leagueId.publish)

  protected def streamTrades: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMap(site.getTrades)
      .flatMap(fs2.Stream.apply(_: _*))
      .evalMap(databaseService.storeTrade)

  protected def streamLeagueSettings: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMap(site.getSettings)
      .evalMap(databaseService.storeLeagueSettings)
}

package fantasycalc.tradeparser.services.fantasysite

import cats.effect.Concurrent
import cats.{Monad, MonadError}
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream
import fantasycalc.tradeparser.util.ImplicitStreamOps._

/**
  * TODO: Rethink how error handling is done with these streams.
  * Right now it's a lot of catching errors via MonadError or brute retries.
  */
class FantasySiteUpdateService[F[_]: Monad: Concurrent](
  site: FantasySiteService[F],
  topics: Topics[F],
  databaseService: DatabaseService[F]
)(implicit monadError: MonadError[F, Throwable]) {

  def stream: Stream[F, Any] =
    Stream(
      restartStreamOnError(streamTrades),
      restartStreamOnError(streamLeagueSettings),
      streamLeagueIds
    ).parJoin(3)

  def restartStreamOnError[A](stream: Stream[F, A]): Stream[F, Any] =
    stream.handleErrorWith(err => Stream(Left(err)) ++ stream)

  def streamLeagueIds: Stream[F, LeagueId] =
    Stream
      .evalSeq(site.getLeagues)
      .evalTap(databaseService.storeLeagueId)
      .evalTap(topics.leagueId.publish1)

  def streamTrades: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMapSafe(site.getTrades)
      .flattenList
      .evalMapSafe(databaseService.storeTrade)

  def streamLeagueSettings: Stream[F, Int] =
    topics.leagueId
      .subscribe(maxQueued = 100000)
      .evalMapSafe(site.getSettings)
      .evalMapSafe(databaseService.storeLeagueSettings)
}

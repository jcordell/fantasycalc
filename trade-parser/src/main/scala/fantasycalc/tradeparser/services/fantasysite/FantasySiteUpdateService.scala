package fantasycalc.tradeparser.services.fantasysite

import cats.effect.{Concurrent, Temporal}
import cats.{Monad, MonadError}
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream
import fantasycalc.tradeparser.util.ImplicitStreamOps._

import scala.concurrent.duration._

/**
  * TODO: Rethink how error handling is done with these streams.
  * Right now it's a lot of catching errors via MonadError or brute retries.
  */
class FantasySiteUpdateService[F[_]: Monad: Concurrent: Temporal](
  site: FantasySiteService[F],
  topics: Topics[F],
  databaseService: DatabaseService[F],
  rateLimit: FiniteDuration
)(implicit monadError: MonadError[F, Throwable]) {

  def stream: Stream[F, Any] =
    Stream(
      streamTrades.withRestartOnError,
      streamLeagueSettings.withRestartOnError,
      streamLeagueIds
    ).parJoin(3)

  def streamLeagueIds: Stream[F, LeagueId] =
    Stream
      .evalSeq(site.getLeagues)
      .metered(rateLimit)
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
      .evalMapSafe(
        settings => databaseService.storeLeague(settings.leagueId, settings)
      )
}

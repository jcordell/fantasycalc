package fantasycalc.tradeparser.services.fantasysite

import cats.effect.{Concurrent, Temporal}
import cats.{Monad, MonadError}
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.services.database.DatabaseService
import fs2.Stream
import fantasycalc.tradeparser.util.ImplicitStreamOps._
import cats.implicits._

import scala.concurrent.duration._

/**
  * TODO: Rethink how error handling is done with these streams.
  * Right now it's a lot of catching errors via MonadError or brute retries.
  */
class FantasySiteUpdateService[F[_]: Monad: Concurrent: Temporal](
  site: FantasySiteService[F],
  databaseService: DatabaseService[F],
  rateLimit: FiniteDuration
)(implicit monadError: MonadError[F, Throwable]) {

  def stream: Stream[F, Int] =
    Stream
      .evalSeq(site.getLeagues)
      .metered(rateLimit)
      .evalTapSafe(updateLeagueSettings)
      .flatMap(t => updateTrades(t))

  def updateTrades(leagueId: LeagueId): Stream[F, Int] =
    Stream[F, LeagueId](leagueId)
      .evalMapSafe(site.getTrades)
      .flattenList
      .evalMapSafe(databaseService.storeTrade)

  def updateLeagueSettings(leagueId: LeagueId): F[Int] = {
    for {
      settings <- site.getSettings(leagueId)
      numUpdatedRows <- databaseService.storeLeague(leagueId, settings)
    } yield numUpdatedRows
  }
}

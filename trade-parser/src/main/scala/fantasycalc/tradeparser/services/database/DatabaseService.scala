package fantasycalc.tradeparser.services.database

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.models.{LeagueId, LeagueSettings, Trade}

trait DatabaseService[F[_]] {

  def storeLeagueId(leagueId: LeagueId): F[Int]

  def storeLeagueSettings(leagueSettings: LeagueSettings): F[Int]

  def storeTrade(trade: Trade): F[Int]

}

class PostgresDatabaseService(xa: Aux[IO, Unit]) extends DatabaseService[IO] {

  override def storeLeagueId(leagueId: LeagueId): IO[Int] = {
    sql"insert into Leagues (leagueId, siteId) values ($leagueId, 1)".update.run.transact(xa)
  }

  override def storeTrade(trade: Trade): IO[Int] = {
    sql"${insertTrade(trade.leagueId)}".update.run.transact(xa)
  }

  def insertTrade(leagueId: LeagueId) = s"insert into Trades (leagueId) values ($leagueId)"

  private val InsertTradedPlayer =
    "insert into TradedPlayers (playerId, tradeId, sideId) values (?, ?, ?)"

  override def storeLeagueSettings(leagueSettings: LeagueSettings): IO[Int] = ???
}

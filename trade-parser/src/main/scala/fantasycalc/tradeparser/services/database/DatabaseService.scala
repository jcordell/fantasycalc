package fantasycalc.tradeparser.services.database

import java.time.Instant
import java.util.UUID

import cats.effect.IO
import cats.effect.std.UUIDGen
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor.Aux
import doobie.util.update.Update
import fantasycalc.tradeparser.models._
// This import is required but intelliJ doesn't recognize it as needed.
//import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.implicits.legacy.instant._
import cats.implicits._
import doobie.hi._
import java.sql.Timestamp
import doobie.implicits.javasql._
import doobie.implicits.javatime._

trait DatabaseService[F[_]] {

  def storeLeague(leagueId: LeagueId, settings: LeagueSettings): F[Int]

  def storeTrade(trade: Trade): F[Int]

  def getPlayers: F[List[Player]]
}

class PostgresDatabaseService(xa: Aux[IO, Unit]) extends DatabaseService[IO] {

  override def storeLeague(leagueId: LeagueId,
                           settings: LeagueSettings): IO[Int] = {
    // TODO: real siteID
    sql"insert into Leagues values (${leagueId.id}, 1, ${settings.numTeams}, ${settings.ppr}, ${settings.starters.quarterback}, ${settings.isDynasty}) on conflict do nothing".update.run
      .transact(xa)
  }

  override def storeTrade(trade: Trade): IO[Int] =
    for {
      uuid <- UUIDGen[IO].randomUUID
      numInsertedTrades <- insertTrade(uuid, trade.leagueId, trade.date).update.run
        .transact(xa)
      // Don't attempt to insert duplicate trades
      numInsertedTradedPlayers <- if (numInsertedTrades == 0) IO.pure(0)
      else storeTradedPlayers(trade, uuid)
    } yield numInsertedTradedPlayers

  def storeTradedPlayers(trade: Trade, uuid: UUID): IO[Int] = {
    val sqlInserts: List[Fragment] =
      generateTradedPlayerInsertionSql(trade, uuid)
    val queries = sqlInserts.traverse(_.update.run)
    queries.transact(xa).map(_.sum)
  }

  private def insertTrade(tradeId: UUID,
                          leagueId: LeagueId,
                          timestamp: Instant): Fragment =
    sql"insert into Trades VALUES (${tradeId.toString}::uuid, ${leagueId.id}, ${Timestamp.from(timestamp)}) on conflict do nothing"

  private def generateTradedPlayerInsertionSql(
    trade: Trade,
    tradeId: UUID
  ): List[Fragment] = {
    def generateTradedPlayerSql(uuid: UUID,
                                asset: FantasycalcAssetId,
                                tradeSide: Int) =
      sql"INSERT INTO TradedPlayers VALUES (${uuid.toString}::uuid, ${asset.id}, $tradeSide)"

    trade.side1.map(asset => generateTradedPlayerSql(tradeId, asset, 1)) ++
      trade.side2.map(asset => generateTradedPlayerSql(tradeId, asset, 2))
  }

  override def getPlayers: IO[List[Player]] = {
    sql"SELECT * from Players".query[Player].stream.compile.toList.transact(xa)
  }
}

package fantasycalc.tradeparser.services.database

import java.util.UUID

import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits._
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.models._

trait DatabaseService[F[_]] {

  def storeLeagueId(leagueId: LeagueId): F[Int]

  def storeLeagueSettings(leagueSettings: LeagueSettings): F[Int]

  def storeTrade(trade: Trade): F[Int]

}

class PostgresDatabaseService(xa: Aux[IO, Unit]) extends DatabaseService[IO] {

  override def storeLeagueId(leagueId: LeagueId): IO[Int] = {
    sql"insert into Leagues (leagueId, siteId) values ($leagueId, 1) on conflict do nothing".update.run
      .transact(xa)
  }

  override def storeTrade(trade: Trade): IO[Int] =
    for {
      uuid <- UUIDGen[IO].randomUUID
      _ <- insertTrade(uuid, trade.leagueId).update.run.transact(xa)
      sqlInserts: List[Fragment] = generateTradedPlayerInsertionSql(trade, uuid)
      queries = sqlInserts.traverse(_.update.run)
      updates <- queries.transact(xa)
    } yield updates.sum

  private def insertTrade(tradeId: UUID, leagueId: LeagueId): Fragment =
    sql"insert into Trades VALUES (${tradeId.toString}::uuid, ${leagueId.id})"

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

  override def storeLeagueSettings(leagueSettings: LeagueSettings): IO[Int] =
    IO.pure(1)
}

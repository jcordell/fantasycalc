package fantasycalc.tradeparser.services.database

import cats.effect.IO
import java.util.UUID

import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits._
import cats._, cats.data._, cats.implicits._
import doobie._, doobie.implicits._
import io.circe._, io.circe.jawn._, io.circe.syntax._
import java.awt.Point
import org.postgresql.util.PGobject
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.models._
import enumeratum._
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntime.global
import com.dimafeng.testcontainers.{ForEachTestContainer, PostgreSQLContainer}
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl.MflId
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.flywaydb.core.api.output.MigrateResult
import org.joda.time.Instant
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, ParallelTestExecution}

// TODO: Better test assertions once able to fetch data from DB
class PostgresDatabaseServiceTest
    extends AnyFunSpec
    with ForEachTestContainer
    with BeforeAndAfterEach
    with ParallelTestExecution
    with Matchers {
  implicit val runtime: IORuntime = global

  override val container: PostgreSQLContainer = PostgreSQLContainer()

  override def beforeEach(): Unit = {
    runMigrations
    insertMockPlayers().unsafeRunSync()
  }

  lazy val databaseConnection: Aux[IO, Unit] =
    Transactor.fromDriverManager[IO](
      driver = container.driverClassName,
      url = container.jdbcUrl,
      user = container.username,
      pass = container.password
    )

  def runMigrations: MigrateResult = {
    container.start()

    val configuration = new ClassicConfiguration()
    configuration.setDataSource(
      container.container.getJdbcUrl,
      container.container.getUsername,
      container.container.getPassword
    )
    configuration.setLocations(new Location("classpath:db/migration"))

    val flyway = new Flyway(configuration)
    flyway.migrate()
  }

  def insertMockPlayers(): IO[Int] = {
    val player = Player(
      FantasycalcAssetId("1"),
      PlayerName("player name"),
      MflId("100"),
      Position.WR
    )
    sql"INSERT INTO Players VALUES (${player.id.id}, ${player.name.name}, ${player.mflId.id}, ${player.position.entryName}".update.run
      .transact(databaseConnection)
  }

  private val leagueId: LeagueId = LeagueId("1")
  private val leagueSettings =
    LeagueSettings(leagueId, 10, Starters(1.5, 2, 2, 1), 0, isDynasty = true)

  describe("storeLeague") {
    it("should insert league ids") {
      val postgresDatabaseService =
        new PostgresDatabaseService(databaseConnection)

      val actual = postgresDatabaseService.storeLeague(leagueId, leagueSettings)
      actual.unsafeRunSync() shouldBe 1
    }
  }

  describe("storeTrades") {
    it("should insert correct number of trades") {
      val postgresDatabaseService =
        new PostgresDatabaseService(databaseConnection)
      val trade = Trade(
        leagueId,
        Instant.now,
        List(FantasycalcAssetId("1")),
        List(FantasycalcAssetId("2"))
      )

      val actual = (for {
        _ <- postgresDatabaseService.storeLeague(leagueId, leagueSettings)
        result <- postgresDatabaseService.storeTrade(trade)
      } yield result).unsafeRunSync()

      actual shouldBe 2
    }
  }
}

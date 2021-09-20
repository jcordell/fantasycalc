package fantasycalc.tradeparser.services.database

import java.time.Instant

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntime.global
import com.dimafeng.testcontainers.{ForEachTestContainer, PostgreSQLContainer}
import doobie.Transactor
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl.MflId
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.flywaydb.core.api.output.MigrateResult
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterEach, OneInstancePerTest, ParallelTestExecution}

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

    val player = Player(
      FantasycalcAssetId(1),
      PlayerName("player name"),
      MflId("100"),
      Position.WR
    )
    insertMockPlayer(player).unsafeRunSync()
    insertMockPlayer(player.copy(id = FantasycalcAssetId(2))).unsafeRunSync()
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

  def insertMockPlayer(player: Player): IO[Int] = {
    sql"INSERT INTO Players VALUES (${player.id.id}, ${player.name.name}, ${player.mflId.id}, ${player.position.entryName})".update.run
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
    it("should insert trades") {
      val postgresDatabaseService =
        new PostgresDatabaseService(databaseConnection)
      val trade = Trade(
        leagueId,
        Instant.now,
        List(FantasycalcAssetId(1)),
        List(FantasycalcAssetId(2))
      )

      val actual = (for {
        _ <- postgresDatabaseService.storeLeague(leagueId, leagueSettings)
        result <- postgresDatabaseService.storeTrade(trade)
      } yield result).unsafeRunSync()

      actual shouldBe 2
    }

    it("should not insert duplicate trade") {
      val postgresDatabaseService =
        new PostgresDatabaseService(databaseConnection)
      val trade = Trade(
        leagueId,
        Instant.now,
        List(FantasycalcAssetId(1)),
        List(FantasycalcAssetId(2))
      )

      val actual = (for {
        _ <- postgresDatabaseService.storeLeague(leagueId, leagueSettings)
        result <- postgresDatabaseService.storeTrade(trade)
        secondInsertResult <- postgresDatabaseService.storeTrade(trade)
      } yield result + secondInsertResult).unsafeRunSync()

      actual shouldBe 2
    }
  }
}

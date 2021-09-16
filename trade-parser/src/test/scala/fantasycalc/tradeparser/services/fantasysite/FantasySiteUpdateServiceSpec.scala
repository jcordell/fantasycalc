package fantasycalc.tradeparser.services.fantasysite

import cats.Monad
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntime.global
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream
import fs2.concurrent.Topic
import org.joda.time.Instant
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
  * TODO: Fix/reimplement test.
  * Topics aren't working correctly in test code so getTrades/getSettings are never invoked.
  */

/**
class FantasySiteUpdateServiceSpec extends AnyFunSpec with Matchers {
  implicit val runtime: IORuntime = global

  /**
    * TODO: Fix this test. It hangs, not sure why. It doesn't timeout during production code.
    */
  ignore("stream") {
    val expectedLeagueIds = List(LeagueId("1"))

    val fantasySiteService = new FantasySiteService[IO] {
      override def getLeagues: IO[List[LeagueId]] = {
        IO.pure(expectedLeagueIds)
      }
      override def getTrades(leagueId: LeagueId): IO[List[Trade]] = {
        println("getting trades")
        IO.pure(
          List(
            Trade(
              LeagueId("1"),
              Instant.now,
              List(FantasycalcAssetId("1")),
              List(FantasycalcAssetId("2"))
            )
          )
        )
      }

      override def getSettings(leagueId: LeagueId): IO[LeagueSettings] =
        IO.pure(
          LeagueSettings(
            leagueId = LeagueId("1"),
            numTeams = 10,
            starters = Starters(1, 1, 1, 1),
            ppr = .5
          )
        )
    }

    val topicIO = Topic.apply[IO, LeagueId]

    val database = new InMemoryDatabaseService[IO]
    val value: Stream[IO, Any] = for {
      topic: Topic[IO, LeagueId] <- Stream.eval(topicIO)
      service = new FantasySiteUpdateService[IO](
        fantasySiteService,
        Topics(leagueId = topic),
        database
      )
      aa <- service.stream
    } yield { aa }

    value.compile.toList.unsafeRunSync()
    database.state.storedLeagueIds shouldBe expectedLeagueIds
  }

}
*/

// TODO: Refactor to use State monad
/**
class InMemoryDatabaseService[F[_]: Monad] extends DatabaseService[F] {
  case class State(storedLeagueSettings: List[LeagueSettings],
                   storedTrades: List[Trade],
                   storedLeagueIds: List[LeagueId])

  var state: State = State(List.empty, List.empty, List.empty)

  override def storeLeagueId(leagueId: LeagueId): F[Int] = {
    state = state.copy(storedLeagueIds = state.storedLeagueIds :+ leagueId)
    Monad[F].pure(1)
  }

  override def storeLeagueSettings(leagueSettings: LeagueSettings): F[Int] = {
    state = state.copy(storedLeagueSettings = state.storedLeagueSettings :+ leagueSettings)
    Monad[F].pure(1)
  }

  override def storeTrade(trade: Trade): F[Int] = {
    state = state.copy(storedTrades = state.storedTrades :+ trade)
    Monad[F].pure(1)
  }
}
*/

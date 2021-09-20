package fantasycalc.tradeparser

import cats.effect.{ExitCode, IO, IOApp}
import fantasycalc.tradeparser.modules.DatabaseModule

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    TradeparserServer.stream[IO](DatabaseModule.postgresDatabaseService).compile.drain.as(ExitCode.Success)
}

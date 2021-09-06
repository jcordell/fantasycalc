package fantasycalc.tradeparser

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    TradeparserServer.stream[IO].compile.drain.as(ExitCode.Success)
}

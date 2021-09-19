package fantasycalc.tradeparser

import cats.effect.{Async, ExitCode}
import fantasycalc.tradeparser.clients.MflClientImpl
import fantasycalc.tradeparser.modules.FantasySiteModule
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.fantasysite.FantasySiteUpdateService
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter
import fantasycalc.tradeparser.services.fantasysite.scrapers.MflService
import fs2.Stream
import org.http4s.client.Client
import org.http4s.client.middleware._
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration._

object TradeparserServer {

  def stream[F[_]: Async](
    databaseService: DatabaseService[F]
  ): Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      httpClient: Client[F] = ResponseLogger(logHeaders = true, logBody = true)(
        RequestLogger[F](logHeaders = true, logBody = true)(
          FollowRedirect.apply(5)(client)
        )
      )

      // TODO: Refactor these to modules
      mflModule = new FantasySiteModule[F](httpClient)
      mflClient = new MflClientImpl[F](httpClient)

      players <- Stream.eval(databaseService.getPlayers)
      playerIdConverter = new PlayerIdConverter(players)

      mflService = new MflService[F](mflClient, playerIdConverter)

      _ <- new FantasySiteUpdateService[F](mflService, databaseService, 10.seconds).stream

      exitCode = ExitCode.Success
    } yield exitCode
  }.drain
}

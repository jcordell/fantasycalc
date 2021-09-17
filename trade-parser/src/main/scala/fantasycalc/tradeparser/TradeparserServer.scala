package fantasycalc.tradeparser

import cats.effect.{Async, ExitCode}
import fantasycalc.tradeparser.clients.MflClientImpl
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.modules.FantasySiteModule
import fantasycalc.tradeparser.services.database.DatabaseService
import fantasycalc.tradeparser.services.fantasysite.FantasySiteUpdateService
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter
import fantasycalc.tradeparser.services.fantasysite.scrapers.MflService
import fantasycalc.tradeparser.services.messaging.Topics
import fs2.Stream
import fs2.concurrent.Topic
import org.http4s.client.Client
import org.http4s.client.middleware.{
  FollowRedirect,
  Logger,
  RequestLogger,
  ResponseLogger
}
import org.http4s.ember.client.EmberClientBuilder

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

      players <- Stream.eval(databaseService.getPlayers)
      _ = println(players)

      // TODO: Refactor these to modules
      mflModule = new FantasySiteModule[F](httpClient)
      mflClient = new MflClientImpl[F](httpClient)
      playersApiResponse <- Stream.eval(mflClient.getPlayers)
      playerIdConverter = new PlayerIdConverter(playersApiResponse)
      mflService = new MflService[F](mflClient, playerIdConverter)
      leagueIdTopic <- Stream.eval(Topic.apply[F, LeagueId])
      topics = Topics(leagueIdTopic)

      _ <- new FantasySiteUpdateService[F](mflService, topics, databaseService).stream

      exitCode = ExitCode.Success
    } yield exitCode
  }.drain
}

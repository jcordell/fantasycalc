package fantasycalc.tradeparser

import cats.effect.{Async, ExitCode, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fantasycalc.tradeparser.modules.FantasySiteModule
import fantasycalc.tradeparser.services.fantasysite.FantasySiteUpdateService
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object TradeparserServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      mflModule = new FantasySiteModule[F](client)
      hmm <- FantasySiteUpdateService.mock(mflModule.mflService)
      _ = println(hmm)

//      helloWorldAlg = HelloWorld.impl[F]
//      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
//      httpApp = (
//        TradeparserRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
//        TradeparserRoutes.jokeRoutes[F](jokeAlg)
//      ).orNotFound

      // With Middlewares in place
//      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)
//
//      exitCode <- Stream.resource(
//        EmberServerBuilder.default[F]
//          .withHost(ipv4"0.0.0.0")
//          .withPort(port"8080")
//          .withHttpApp(finalHttpApp)
//          .build >>
//        Resource.eval(Async[F].never)
//      )

      exitCode = ExitCode.Success
    } yield exitCode
  }.drain
}

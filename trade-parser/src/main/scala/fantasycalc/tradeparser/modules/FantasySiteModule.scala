package fantasycalc.tradeparser.modules

import cats.effect.Concurrent
import fantasycalc.tradeparser.clients.MFlClient
import fantasycalc.tradeparser.services.fantasysite.scrapers.MflService
import org.http4s.client.Client

class FantasySiteModule[F[_]: Concurrent](httpClient: Client[F]) {
  val mflClient = new MFlClient[F](httpClient)
  val mflService = new MflService[F](mflClient)
}

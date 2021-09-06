package fantasycalc.tradeparser.clients

import cats.effect.Concurrent
import fantasycalc.tradeparser.models.ImplicitEntityCodecs._
import fantasycalc.tradeparser.models.api.mfl.LeagueSearchApiResponse
import io.circe.generic.auto._
import org.http4s.client.Client


class MFlClient[F[_]: Concurrent](httpClient: Client[F]) {
  private val YEAR = 2021
  private val BASE_URL = s"https://api.myfantasyleague.com/$YEAR"

  def searchLeagues(search: String): F[LeagueSearchApiResponse] = {
    val url = s"$BASE_URL/export?TYPE=leagueSearch&SEARCH=$search&JSON=1"
    httpClient.expect[LeagueSearchApiResponse](url)
  }
}

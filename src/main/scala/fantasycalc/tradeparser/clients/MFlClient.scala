package fantasycalc.tradeparser.clients

import cats.effect.Concurrent
import fantasycalc.tradeparser.models.ImplicitEntityCodecs._
import fantasycalc.tradeparser.models.LeagueId
import fantasycalc.tradeparser.models.api.mfl.MflPositionRules._
import fantasycalc.tradeparser.models.api.mfl._
import io.circe.generic.auto._
import org.http4s.client.Client

trait MflClient[F[_]] {
  def searchLeagues(search: String): F[LeagueSearchApiResponse]

  def getPlayers: F[PlayersApiResponse]

  def getTrades(leagueId: LeagueId): F[TradesApiResponse]

  def getRules(leagueId: LeagueId): F[RulesApiResponse]

  def getLeague(leagueId: LeagueId): F[LeagueApiResponse]
}

class MflClientImpl[F[_]: Concurrent](httpClient: Client[F])
    extends MflClient[F] {
  private val YEAR = 2021
  private val API_TOKEN = ""
  private val BASE_URL = s"https://api.myfantasyleague.com/$YEAR"

  def searchLeagues(search: String): F[LeagueSearchApiResponse] = {
    val url = s"$BASE_URL/export?TYPE=leagueSearch&SEARCH=$search&JSON=1&APIKEY=$API_TOKEN"
    httpClient.expect[LeagueSearchApiResponse](url)
  }

  def getPlayers: F[PlayersApiResponse] = {
    val url = s"$BASE_URL/export?TYPE=players&SEARCH&JSON=1&APIKEY=$API_TOKEN"
    httpClient.expect[PlayersApiResponse](url)
  }

  def getTrades(leagueId: LeagueId): F[TradesApiResponse] = {
    val url =
      s"$BASE_URL/export?TYPE=transactions&L=${leagueId.id}&APIKEY=$API_TOKEN&W=&TRANS_TYPE=TRADE&FRANCHISE=&DAYS=&COUNT=&JSON=1"
    httpClient.expect[TradesApiResponse](url)
  }

  def getLeague(leagueId: LeagueId): F[LeagueApiResponse] = {
    val url =
      s"$BASE_URL/export?TYPE=league&L=${leagueId.id}&JSON=1"
    httpClient.expect[LeagueApiResponse](url)
  }

  def getRules(leagueId: LeagueId): F[RulesApiResponse] = {
    val url =
      s"$BASE_URL/export?TYPE=rules&L=${leagueId.id}&JSON=1&APIKEY=$API_TOKEN"
    httpClient.expect[RulesApiResponse](url)
  }
}

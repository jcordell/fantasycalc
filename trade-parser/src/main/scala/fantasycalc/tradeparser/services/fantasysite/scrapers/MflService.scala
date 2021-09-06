package fantasycalc.tradeparser.services.fantasysite.scrapers

import cats.effect.Concurrent
import cats.implicits._
import fantasycalc.tradeparser.clients.MFlClient
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.services.fantasysite.FantasySiteService

class MflService[F[_]: Concurrent](mflClient: MFlClient[F]) extends FantasySiteService[F, FantasySite.MFL.type] {

  // TODO: Search term should be 'a' to 'z' to fetch all leagues
  override def getLeagues: F[List[LeagueId]] = for {
      leaguesResponse <- mflClient.searchLeagues("dynasty")
    } yield leaguesResponse.leagues.league.map(league => LeagueId(league.id))

  override def getTrades(leagueId: LeagueId): F[List[Trade]] = ???

  override def getSettings(leagueId: LeagueId): F[List[LeagueSettings]] = ???
}

package fantasycalc.tradeparser.services.fantasysite

import fantasycalc.tradeparser.models._

trait FantasySiteService[F[_]] {
  def getLeagues: F[List[LeagueId]]

  def getTrades(leagueId: LeagueId): F[List[Trade]]

  def getSettings(leagueId: LeagueId): F[LeagueSettings]
}

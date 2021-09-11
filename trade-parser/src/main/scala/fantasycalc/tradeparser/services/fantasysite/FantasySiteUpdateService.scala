package fantasycalc.tradeparser.services.fantasysite

import cats.Monad
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.services.database.DatabaseService
import fs2.Stream

class FantasySiteUpdateService[F[_]: Monad](databaseService: DatabaseService[F]) {
//  def parseTrades[F[_], T: FantasySite](
//    site: FantasySiteService[F, T]
//  ): Stream[F, List[Trade]] =
//    parse(site, site.getTrades)
//
//  def parseLeagueSettings[F[_], T <: FantasySite](
//    site: FantasySiteService[F, T]
//  ): Stream[F, List[LeagueSettings]] =
//    parse(site, site.getSettings)
//
  def mock[T <: FantasySite](
    site: FantasySiteService[F, T]
  ): Stream[F, String] =
    Stream
      .evalSeq(site.getLeagues)
      .evalMap(mockFn)

  private def mockFn[T <: FantasySite]: LeagueId => F[String] = {
    (id: LeagueId) => Monad[F].pure(id.id)
  }

//  private def parse[F[_], LeagueAttributeT, T <: FantasySite](
//    site: FantasySiteService[F, T],
//    parseFn: LeagueId => F[List[LeagueAttributeT]]
//  ): Stream[F, List[LeagueAttributeT]] = {
//    Stream
//      .eval(site.getLeagues)
//      .flatMap(x => fs2.Stream.apply(x: _*))
//      .evalMap(parseFn)
//  }
}

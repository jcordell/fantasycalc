package fantasycalc.tradeparser.services.fantasysite

import cats.Monad
import fantasycalc.tradeparser.models._
import fs2.Stream

object FantasySiteUpdateService {
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
  def mock[F[_]: Monad, T <: FantasySite](
    site: FantasySiteService[F, T]
  ): Stream[F, String] =
    Stream
      .evalSeq(site.getLeagues)
      .evalMap(mockFn)

  private def mockFn[T <: FantasySite, F[_] : Monad]: LeagueId => F[String] = {
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

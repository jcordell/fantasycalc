package fantasycalc.tradeparser.services.fantasysite.scrapers

import cats.Monad
import cats.implicits._
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl._
import fantasycalc.tradeparser.services.fantasysite.FantasySiteService
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter
import org.joda.time.Instant

class MflService[F[_]: Monad](mflClient: MflClient[F],
                              playerIdConverter: PlayerIdConverter)
    extends FantasySiteService[F] {

  // TODO: Search term should be 'a' to 'z' to fetch all leagues
  override def getLeagues: F[List[LeagueId]] =
    for {
      leaguesResponse <- mflClient.searchLeagues("dynasty")
    } yield leaguesResponse.leagues.league.map(league => LeagueId(league.id))

  override def getTrades(leagueId: LeagueId): F[List[Trade]] =
    for {
      trades <- mflClient.getTrades(leagueId)
    } yield {
      trades.transactions.transaction.map(trade => {
        Trade(
          leagueId,
          Instant.ofEpochSecond(trade.timestamp.toInt),
          toTradeList(trade.franchise1_gave_up),
          toTradeList(trade.franchise2_gave_up)
        )
      })
    }

  private def toTradeList(tradeString: String): List[FantasycalcAssetId] =
    tradeString
      .split(",")
      .toList
      .map(MflId.apply)
      .flatMap(playerIdConverter.toFantasycalcAssetId)

  override def getSettings(leagueId: LeagueId): F[LeagueSettings] = {
    for {
      league <- mflClient.getLeague(leagueId)
      rules <- mflClient.getRules(leagueId)
    } yield {
      LeagueSettings(
        leagueId,
        // TODO: remove .toInt
        league.league.franchises.count.toInt,
        parseStartingRules(league.league.starters),
        parsePpr(rules.rules.positionRules),
        isDynasty = true // TODO
      )
    }
  }

  private def parseStartingRules(mflStarterLimits: MflStarterLimits): Starters =
    Starters(
      quarterback = getLimit(mflStarterLimits, MflPosition.QB, "1"),
      runningBack = getLimit(mflStarterLimits, MflPosition.RB, "2"),
      wideReceiver = getLimit(mflStarterLimits, MflPosition.WR, "2"),
      tightEnd = getLimit(mflStarterLimits, MflPosition.TE, "1")
    )

  private def getLimit(mflStarterLimits: MflStarterLimits,
                       position: MflPosition,
                       default: String): Int = {
    val parsedLimitOrDefault = mflStarterLimits.position
      .find(_.name.contains(position.entryName))
      .map(_.limit)
      .getOrElse {
        println(s"Could not find $position in MFL starter limits, using default.")
        default
      }
    parsedLimitOrDefault.toIntOption.getOrElse {
      println(s"Could not parse limit as string value=$parsedLimitOrDefault")
      default.toInt
    }
  }

  private def parsePpr(rules: List[PositionRules]): Int = {
    val wideReceiverRules = rules
      .filter(_.positions.contains(MflPosition.WR.entryName))
      .flatMap(_.rule)
    val parsedPprValueOrDefault = wideReceiverRules
      .find(_.event.`$t`.contains("CC"))
      .map(_.points.`$t`.replace("*", ""))
      .getOrElse {
        println("Could not find PPR value, using default")
        "1"
      }
    parsedPprValueOrDefault.toIntOption.getOrElse {
      println("Could not parse PPR value to Int: " + parsedPprValueOrDefault)
      1
    }
  }
}

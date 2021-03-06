package fantasycalc.tradeparser.services.fantasysite.scrapers

import java.time.Instant

import cats.Monad
import cats.implicits._
import fantasycalc.tradeparser.clients.MflClient
import fantasycalc.tradeparser.models._
import fantasycalc.tradeparser.models.api.mfl._
import fantasycalc.tradeparser.services.fantasysite.FantasySiteService
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter

import scala.util.Try

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
        parsePprRules(parseFormattedPositionRules(rules.rules)),
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
      .map(_.last.toString)
      .getOrElse {
        println(
          s"Could not find $position in MFL starter limits, using default."
        )
        default
      }
    parsedLimitOrDefault.toIntOption.getOrElse {
      println(s"Could not parse limit as string value=$parsedLimitOrDefault")
      default.toInt
    }
  }

  private def parseFormattedPositionRules: MflRules => List[PositionRules] = reformatRule _ andThen reformatPositionRule

  private def reformatRule(rules: MflRules): List[MflPositionRules] = rules match {
    case rule: Rules => rule.positionRules
    case rule: SinglePositionRules => List(rule.positionRules)
  }

  private def reformatPositionRule(rules: List[MflPositionRules]): List[PositionRules] =
    rules.map {
      case rule: PositionRule   => PositionRules(rule.positions, List(rule.rule))
      case rules: PositionRules => rules
    }

  private[scrapers] def parsePprRules(rules: List[PositionRules]): BigDecimal = {
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
    Try(BigDecimal(parsedPprValueOrDefault)).toOption.getOrElse {
      println("Could not parse PPR value to BigDecimal: " + parsedPprValueOrDefault)
      BigDecimal(1)
    }
  }
}

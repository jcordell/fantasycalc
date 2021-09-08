package fantasycalc.tradeparser.models

import org.joda.time._

case class Trade(leagueId: LeagueId,
                 date: Instant,
                 side1: List[FantasycalcAssetId],
                 side2: List[FantasycalcAssetId])

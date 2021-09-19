package fantasycalc.tradeparser.models

import java.time.Instant

case class Trade(leagueId: LeagueId,
                 date: Instant,
                 side1: List[FantasycalcAssetId],
                 side2: List[FantasycalcAssetId])

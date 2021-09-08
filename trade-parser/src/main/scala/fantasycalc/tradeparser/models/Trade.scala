package fantasycalc.tradeparser.models

case class Trade(leagueId: LeagueId,
                 side1: List[FantasycalcAssetId],
                 side2: List[FantasycalcAssetId])

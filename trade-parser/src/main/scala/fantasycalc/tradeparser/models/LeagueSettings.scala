package fantasycalc.tradeparser.models

// Quarterback the only essential value here.
case class Starters(quarterback: BigDecimal,
                    runningBack: Int,
                    wideReceiver: Int,
                    tightEnd: Int)

case class LeagueSettings(leagueId: LeagueId,
                          numTeams: Int,
                          starters: Starters,
                          ppr: BigDecimal)

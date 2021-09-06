package fantasycalc.tradeparser.models.api.mfl

case class LeagueSearchApiResponse(version: String,
                                   leagues: Leagues,
                                   encoding: String)

case class League(homeURL: String, name: String, id: String)
case class Leagues(league: List[League])

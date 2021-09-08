package fantasycalc.tradeparser.models.api.mfl

case class MflPlayer(position: Option[String], name: Option[String], id: String, team: Option[String])

case class Players(player: List[MflPlayer])

case class PlayersApiResponse(version: String,
                              players: Players,
                              encoding: String)

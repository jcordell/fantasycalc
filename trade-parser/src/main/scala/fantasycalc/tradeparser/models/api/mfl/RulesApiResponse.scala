package fantasycalc.tradeparser.models.api.mfl

case class Points(`$t`: String)
case class Rule(points: Points, range: Points, event: Points)
case class PositionRules(positions: String, rule: List[Rule])
case class Rules(positionRules: List[PositionRules])
case class RulesApiResponse(version: String, rules: Rules, encoding: String)

package fantasycalc.tradeparser.models.api.mfl

import io.circe.Decoder
import io.circe.generic.JsonCodec
import cats.syntax.functor._
import io.circe.{Decoder, Encoder}, io.circe.generic.auto._
import io.circe.syntax._
import io.circe._, io.circe.generic.semiauto._

case class Points(`$t`: String)
case class Rule(points: Points, range: Points, event: Points)

// Mfl API can return a single Rule element or a list of them.
sealed trait MflPositionRules
case class PositionRules(positions: String, rule: List[Rule])
    extends MflPositionRules
case class PositionRule(positions: String, rule: Rule) extends MflPositionRules

sealed trait MflRules
case class Rules(positionRules: List[MflPositionRules]) extends MflRules
case class SinglePositionRules(positionRules: MflPositionRules) extends MflRules

case class RulesApiResponse(version: String, rules: MflRules, encoding: String)

object MflPositionRules {
  implicit val mflPositionRulesDecoder: Decoder[MflPositionRules] =
    List[Decoder[MflPositionRules]](
      Decoder[PositionRule].widen,
      Decoder[PositionRules].widen
    ).reduceLeft(_ or _)

  implicit val mflRulesDecoder: Decoder[MflRules] =
    List[Decoder[MflRules]](
      Decoder[Rules].widen,
      Decoder[SinglePositionRules].widen
    ).reduceLeft(_ or _)

  implicit val rulesApiResponseDecoder: Decoder[RulesApiResponse] = deriveDecoder[RulesApiResponse]

}

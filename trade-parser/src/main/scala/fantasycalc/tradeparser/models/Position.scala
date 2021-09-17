package fantasycalc.tradeparser.models

import enumeratum._

sealed trait Position extends EnumEntry

object Position extends Enum[Position] {
  val values: IndexedSeq[Position] = findValues
  implicit val doobieEncoders: doobie.Meta[Position] = Doobie.meta(Position)

  case object TMWR extends Position
  case object TMRB extends Position
  case object TMDL extends Position
  case object TMLB extends Position
  case object TMDB extends Position
  case object TMTE extends Position
  case object Def extends Position
  case object ST extends Position
  case object Off extends Position
  case object TMQB extends Position
  case object TMPK extends Position
  case object TMPN extends Position
  case object Coach extends Position
  case object PK extends Position
  case object QB extends Position
  case object CB extends Position
  case object LB extends Position
  case object TE extends Position
  case object WR extends Position
  case object PN extends Position
  case object RB extends Position
  case object DT extends Position
  case object S extends Position
  case object DE  extends Position
}

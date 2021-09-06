package fantasycalc.tradeparser.models

import enumeratum._

sealed trait FantasySite extends EnumEntry

object FantasySite extends Enum[FantasySite] {
  val values: IndexedSeq[FantasySite] = findValues

  case object MFL   extends FantasySite
}

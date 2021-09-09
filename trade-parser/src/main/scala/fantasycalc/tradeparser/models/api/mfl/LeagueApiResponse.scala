package fantasycalc.tradeparser.models.api.mfl

import enumeratum.{Enum, EnumEntry}


// TODO: draftPlayerPool="Rookie" in example, this might be how to figure out if league is a dynasty or redraft.
//case class MflLeague(id: String,
//                     franchises: Franchises,
//                     draftPlayerPool: String,
//                     starters: MflStarterLimits)


sealed trait MflPosition extends EnumEntry

object MflPosition extends Enum[MflPosition] {
  val values: IndexedSeq[MflPosition] = findValues

  case object QB extends MflPosition
  case object TE extends MflPosition
  case object WR extends MflPosition
  case object RB extends MflPosition
  case object Unknown extends MflPosition

  override def withName(name: String): MflPosition =
    super.withNameInsensitiveOption(name).getOrElse(Unknown)
}

case class MflPositionStarterLimit(name: String, limit: String)
case class MflStarterLimits(count: String, position: List[MflPositionStarterLimit])
case class Division(name: String, id: String)

case class Franchises(count: String)
case class MflLeague(franchises: Franchises,
                  draftPlayerPool: String,
                  id: String,
                  rosterSize: String,
                  name: String,
                  starters: MflStarterLimits)

case class LeagueApiResponse(version: String, league: MflLeague, encoding: String)

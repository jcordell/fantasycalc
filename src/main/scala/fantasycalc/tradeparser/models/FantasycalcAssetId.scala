package fantasycalc.tradeparser.models

/**
  * Internal FantasyCalc ID to make it easier to associate players
  * with different spellings depending on the fantasy site.
  *
  * Can represent players or picks. // TODO: Add picks to database CSV
  */
case class FantasycalcAssetId(id: Int)

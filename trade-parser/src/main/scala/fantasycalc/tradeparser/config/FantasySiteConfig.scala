package fantasycalc.tradeparser.config

import simulacrum.typeclass

@typeclass trait FantasySiteConfig {
  def url: String
}

package fantasycalc.tradeparser.models

import fantasycalc.tradeparser.models.api.mfl.MflId

case class Player(id: FantasycalcAssetId,
                  name: PlayerName,
                  mflId: MflId,
                  position: Position)

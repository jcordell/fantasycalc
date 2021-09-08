package fantasycalc.tradeparser.mocks

import fantasycalc.tradeparser.ApiResponses
import fantasycalc.tradeparser.services.fantasysite.mfl.PlayerIdConverter

class PlayerIdConverterMock
    extends PlayerIdConverter(ApiResponses.Mfl.PlayersResponse) {}

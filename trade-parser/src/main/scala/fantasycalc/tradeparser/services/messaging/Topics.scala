package fantasycalc.tradeparser.services.messaging

import cats.effect.Concurrent
import fantasycalc.tradeparser.models.LeagueId
import fs2.concurrent.Topic

case class Topics[F[_]: Concurrent](leagueId: Topic[F, LeagueId])

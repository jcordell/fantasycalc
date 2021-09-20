package fantasycalc.tradeparser.services.database

import cats.effect.IO
import doobie._
import doobie.util.transactor.Transactor.Aux
import fantasycalc.tradeparser.config.Settings

object DoobieConnection {

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    Settings.Postgres.DRIVER,
    Settings.Postgres.DATABASE_URL,
    Settings.Postgres.USER,
    Settings.Postgres.PASSWORD
  )
}

package fantasycalc.tradeparser.modules

import fantasycalc.tradeparser.services.database.{DoobieConnection, PostgresDatabaseService}

object DatabaseModule {

  val postgresDatabaseService = new PostgresDatabaseService(DoobieConnection.xa)

}

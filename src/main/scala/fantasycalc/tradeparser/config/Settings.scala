package fantasycalc.tradeparser.config

object Settings {
  val env: String = scala.util.Properties.envOrElse("SCALA_ENV", "")

  object Postgres {
    val DRIVER = "org.postgresql.Driver"

    val DATABASE: String = env match {
      case "test" => scala.util.Properties.envOrElse("API_POSTGRES_TEST_DATABASE", "http4s_api_test")
      case _ => scala.util.Properties.envOrElse("API_POSTGRES_DATABASE", "fantasycalc_db")
    }

    val DATABASE_URL = s"jdbc:postgresql:${Settings.Postgres.DATABASE}"

    val USER: String = env match {
      case "test" => scala.util.Properties.envOrElse("API_POSTGRES_TEST_USER", "postgres")
      case _ => scala.util.Properties.envOrElse("API_POSTGRES_USER", "postgres")
    }

    val PASSWORD: String = env match {
      case "test" => scala.util.Properties.envOrElse("API_POSTGRES_TEST_PASS", "")
      case _ => scala.util.Properties.envOrElse("API_POSTGRES_PASS", "password")
    }
  }
}

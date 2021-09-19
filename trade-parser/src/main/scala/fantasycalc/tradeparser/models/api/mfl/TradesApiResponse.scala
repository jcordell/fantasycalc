package fantasycalc.tradeparser.models.api.mfl

case class Transaction(franchise2: String,
                       franchise2_gave_up: String,
                       timestamp: String,
                       by_commish: Option[String],
                       franchise: String,
                       franchise1_gave_up: String,
                       `type`: String,
                       comments: Option[String],
                       expires: String)
case class Transactions(transaction: List[Transaction])
case class TradesApiResponse(version: String,
                             transactions: Transactions,
                             encoding: String)

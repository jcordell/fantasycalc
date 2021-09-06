package fantasycalc.tradeparser.models

import cats.effect._
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}


object ImplicitEntityCodecs {
  implicit def jsonDecoder[F[_]: Concurrent, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit def jsonEncoder[F[_], A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]
}

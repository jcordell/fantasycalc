package fantasycalc.tradeparser.util

import cats.{Monad, MonadError}
import fs2.Stream
import cats.implicits._

object ImplicitStreamOps {
  implicit class implicitStreamOps[F[_], A](stream: Stream[F, A])(
    implicit monadError: MonadError[F, Throwable]
  ) {

    /**
      * Safely attempt effect and emit the successful results, ignoring failures.
      */
    def evalMapSafe[OutputT](fa: A => F[OutputT]): Stream[F, OutputT] =
      stream
        .evalMap(value => {
          MonadError[F, Throwable].attempt(fa(value)).map {
            case Right(value) => Right(value)
            case Left(throwable) =>
              println("Error processing stream", throwable)
              Left(throwable)
          }
        })
        .collect {
          case Right(setting) => setting
        }
  }

  implicit class ImplicitStreamListOps[F[_], A](stream: Stream[F, List[A]]) {
    def flattenList: Stream[F, A] =
      stream.flatMap(Stream.emits)
  }

}

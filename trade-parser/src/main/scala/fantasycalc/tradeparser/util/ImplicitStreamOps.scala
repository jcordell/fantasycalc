package fantasycalc.tradeparser.util

import cats.MonadError
import cats.implicits._
import fs2.Stream

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

    /**
      * Safely attempt effect and emit the successful results, ignoring failures.
      */
    def evalTapSafe[OutputT](fa: A => F[OutputT]): Stream[F, A] =
      stream
        .evalTap(value => {
          MonadError[F, Throwable].attempt(fa(value)).map {
            case Right(value) => Right(value)
            case Left(throwable) =>
              println("Error processing stream", throwable)
              Left(throwable)
          }
        })

    def withRestartOnError: Stream[F, A] =
      stream.handleErrorWith(_ => stream)
  }

  implicit class ImplicitStreamListOps[F[_], A](stream: Stream[F, List[A]]) {
    def flattenList: Stream[F, A] =
      stream.flatMap(Stream.emits)
  }

}

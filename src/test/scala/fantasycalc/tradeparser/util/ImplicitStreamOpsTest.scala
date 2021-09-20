package fantasycalc.tradeparser.util

import cats.Monad
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntime.global
import fantasycalc.tradeparser.util.ImplicitStreamOps._
import fs2.Stream
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ImplicitStreamOpsTest extends AnyFunSpec with Matchers {
  describe("evalMapSafe") {
    it("should ignore errors and return all successful results") {
      implicit val runtime: IORuntime = global

      val stream: Stream[IO, Int] = Stream
        .emit(List(1, 2))
        .flattenList
        .repeat
        .take(2)
        .evalMap(_ => Monad[IO].pure(5))
        .evalMapSafe(_ => {
          if (value == 1)
            Monad[IO].pure(throw new Error("Some error"))
          else Monad[IO].pure(10)
        })
        .evalMap(_ => Monad[IO].pure(15))
      stream.compile.toList.unsafeRunSync() shouldBe List(15, 15)
    }
  }

  describe("flattenList") {
    it("should flatten list into single elements") {
      val actual = Stream
        .emit(List(1, 2, 4, 5))
        .flattenList
        .repeat
        .take(2)
      actual.compile.toList shouldBe List(1, 2)
    }
  }
}

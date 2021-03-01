/*
 * Copyright 2020 Phillip Henry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.odinconsultants.errors

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import cats.{Applicative, FlatMap, Monad, MonadError}
import uk.co.odinconsultants.IOs

import scala.util.control.NoStackTrace

sealed trait UserError   extends NoStackTrace
case object UserNotFound extends UserError
case object UserExists   extends UserError

object ErrorHandlingMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val unattempted: IO[Int]                                          = triggered(IO(1), 1)
    val attempted: IO[Either[Throwable, Int]]                         = unattempted.attempt
    val attemptedLifted: EitherT[IO, Nothing, Either[Throwable, Int]] = EitherT.liftF(attempted)
//    val unattemptedLifted: EitherT[IO, Nothing, Int] = EitherT.liftF(unattempted)
//
//    val attempts: EitherT[IO, Nothing, Either[Throwable, Int]] = for {
//      x <- attemptedLifted
//      y <- unattemptedLifted
//    } yield x

    //    val x: IO[Either[Nothing, Int]] = EitherT.liftF[IO, Nothing, Int](unattempted).value // this would blow up
    val x: IO[Either[Nothing, Either[Throwable, Int]]] = attemptedLifted.value
    val printed                                        = x.flatMap(a => IOs.printOut(a))
    (printed >> variousAttempts).as(ExitCode.Success)
  }

  def variousAttempts: IO[String] = {
    val attempted: IO[Either[Throwable, String]] = doSomethingDangerous[IO, String].attempt

    for {
      left                <- attempted
      _                   <- IOs.printOut(left) // "Left(uk.co.odinconsultants.errors.UserNotFound$)"
      unattempted: IO[Int] = triggered(IO(1), 0)
      right               <- unattempted.attempt // Either[Throwable, A]
      _                   <- IOs.printOut(right) // "Right(1)"
      either              <- EitherT.liftF[IO, Nothing, Int](unattempted).value // Either[A, B]
      _                   <- IOs.printOut(either) // "Right(1)"
    } yield {
      s"left = $left, right = $right, either = $either"
    }
  }

  // this doesn't compile despite being in Practical FP, p213
//  def program[F[_]: MonadError[*[_], UserError], A](
//      fa: F[A],
//      fallback: A
//  ): F[A] = {
//    fa.handleError(_ => fallback)
//  }

  def doSomethingDangerous[G[_], A](implicit G: MonadError[G, Throwable]): G[A] = G.raiseError(UserNotFound)
  def triggered[F[_]: Monad: MonadError[*[_], Throwable], A](fa: F[A], trigger: A): F[A] = {
    FlatMap[F].flatMap(fa) { a: A =>
      if (a == trigger) {
        val x: F[A] = MonadError[F, Throwable].raiseError(UserNotFound)
        x
      } else {
        Applicative[F].pure(a)
      }
    }
  }

}

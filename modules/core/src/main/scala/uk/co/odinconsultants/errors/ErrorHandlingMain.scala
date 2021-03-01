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
    val attempted: IO[Either[Throwable, String]] = doSomethingDangerous[IO, String].attempt

    for {
      left                       <- attempted
      _                          <- IOs.printOut(left)
      unattempted                 = triggered(IO(1), 0)
      right                      <- unattempted.attempt
      _                          <- IOs.printOut(right)
      x: IO[Either[Nothing, Int]] = EitherT.liftF(unattempted).value
      either                     <- x
      _                          <- IOs.printOut(either)
    } yield {
      ExitCode.Success
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

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

package uk.co.odinconsultants.cancellation

import cats.effect.std.Semaphore
import cats.effect._

import scala.concurrent.duration._

object CommitSudoku2 extends IOApp {

  def message(x: Any): IO[Unit] = IO { println(s"${new java.util.Date()}: $x") }

  def safe[F[_]: Concurrent, A, B, MonadCancel](handleItem: A => F[B]): Resource[F, A => F[B]] =
    Resource
      .make(Semaphore[F](1))(_.acquire)
      .map { sem => (a: A) =>
        val permit: Resource[F, Unit] = sem.permit
        val used: F[B]                = permit.use(_ => handleItem(a))
        used //.uncancelable
      }

  def processItem(int: Int): IO[Unit] =
    message(s"Processing ${int}") >>
      IO.sleep(2.seconds) >>
      message(s"Done processing ${int}")

  def processItemUncancellable(x: Int): IO[Unit] = IO.uncancelable { _ =>
    val io = message(s"In uncancellable block: $x") >>
      IO.sleep(3.second) >>
      message(s"Finished sleeping with $x")
    loop(_ => io)(x)
  }

  def loop(f: Int => IO[Unit])(a: Int): IO[Unit] = f(a) >> loop(f)(a + 1)

  override def run(args: List[String]): IO[ExitCode] = {
    safe(processItemUncancellable).use { f =>
      val loopCounting: IO[Unit] = loop(f)(0)

      val proc = for {
        t <- loopCounting.start
        _ <- processItem(-99)
        _ <- t.cancel
      } yield IO {
        println("Cancelled")
      }

      proc.as(ExitCode.Success)
    }
  }
}

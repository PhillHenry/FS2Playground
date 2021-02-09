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

import cats.effect.{ExitCode, IO, IOApp}
import fs2._
import cats.implicits._
import uk.co.odinconsultants.IOs

object ErrorsMain extends IOApp {

  def errorHandler: Throwable => IO[Unit] = { t => IOs.stackTrace(t) }

  override def run(args: List[String]): IO[ExitCode] = {
    Stream(1)
      .covary[IO]
      .evalMap(_ => IO(throw new Exception("oops")))
      .onError(_ => Stream.eval(IO.unit))
      .compile
      .drain
      .as(ExitCode.Success)
  }
}

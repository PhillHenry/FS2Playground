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

  type ErrorHandler = PartialFunction[Throwable, Stream[IO, Unit]]
  val errorHandler: ErrorHandler = _ match { case t => Stream.eval(IOs.stackTrace(t)) }
  def evilErrorHandler: ErrorHandler = _ match { case t => IOs.evil(t) }

  override def run(args: List[String]): IO[ExitCode] = {
    val streamed = Stream(1)
      .covary[IO]
      .evalMap(_ => IO(throw new Exception("oops")))
      .onError(_ => Stream.eval(IO.unit))
//      .onError(errorHandler)
      .compile
      .drain
    streamed >> IOs.printOut("That's all folks").as(ExitCode.Success)
  }
}

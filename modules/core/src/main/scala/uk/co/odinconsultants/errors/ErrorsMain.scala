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
import cats.implicits._
import fs2._
import uk.co.odinconsultants.IOs
import uk.co.odinconsultants.Streams.evilErrorHandler

object ErrorsMain extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    streamOnError >> IOs.printOut("That's all folks").as(ExitCode.Success)
  }

  /** Fabio Labella @SystemFw 12:59 I absolutely agree that this is confusing, but the issue is eval_
    * that returns Stream[IO, Nothing] meaning that whatever flatMap operation after that doesn't get
    * executed (just like List().flatMap(f) doesn't execute f)
    * onError boils down to f(e) >> raiseError(e) , and therefore >> gets skipped
    */
  val streamOnError: IO[Unit] = {
    // original Gitter question says `eval_` [note underscore]. This is deprecated in v3.
    // `exec` catches the exception, `eval` throws it.
    val streamed: IO[Unit] = Stream(1)
      .covary[IO]
      .evalMap(_ => IO(throw new Exception("oops")))
//      .onError(_ => Stream.exec(IO.unit)) // happy path
//      .onError(errorHandler) // blows up
      .onError(evilErrorHandler)
      .compile
      .drain
    streamed.guarantee(IOs.printOut("Essentially, this is the finalizer").void).void
  }
}

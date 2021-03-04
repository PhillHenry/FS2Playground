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

package uk.co.odinconsultants.fs2

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream

import scala.concurrent.duration.DurationInt

object GroupWithinMain extends IOApp {

  /** Fabio Labella @SystemFw Mar 01 10:23
    * @DGolubets groupWithin is slightly push based (with backpressure) because it needs some concurrency
    * which chunkN does not it is possible to introduce additional synchronisation to avoid this
    * I think, but we considered not worth it
    */
  override def run(args: List[String]): IO[ExitCode] = {
    val stream = Stream
      .range(1, 100000)
      .evalTap(i => IO(println(s"pulled $i")))
      .groupWithin(10, 10.seconds)
      .take(3)
    stream.compile.toList.as(ExitCode.Success)
  }

}

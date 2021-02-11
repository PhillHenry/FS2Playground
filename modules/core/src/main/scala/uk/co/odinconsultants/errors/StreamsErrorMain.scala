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
import uk.co.odinconsultants.Streams._
import uk.co.odinconsultants.IOs._

object StreamsErrorMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val another10: fs2.Stream[IO, Int] = printing(10, 11)
    val s                              = blowsUpAfter(10) ++ another10
    s.compile.toList.map(printOut).as(ExitCode.Success)
  }

  def test() = {
    import uk.co.odinconsultants.Runner._
    import uk.co.odinconsultants.Streams._

    unsafeRunAndLog(
      (blowsUpAfter(10) ++ printing(10, 11)).compile.toList
    )
  }
}

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

package uk.co.odinconsultants

import cats.effect.IO
import fs2.{Pure, Stream}

object Streams {

  import IOs._

  val blowUp: Stream[IO, Int] = Stream.eval(evil(-1))

  def blowsUpAfter(n: Int): Stream[IO, Int] =
    printing(n) ++ blowUp

  def decorate[T](x: T): String = s"x = $x"

  def printAndReturn[T](x: T): T = {
    println(decorate(x))
    x
  }

  def printing(n: Int): Stream[IO, Int] =
    Stream.emits(1 to n).map(printAndReturn)
}

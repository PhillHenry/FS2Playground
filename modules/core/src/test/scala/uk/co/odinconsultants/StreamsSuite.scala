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

import munit.CatsEffectSuite
import uk.co.odinconsultants.IOs.exception
import uk.co.odinconsultants.Runner._
import uk.co.odinconsultants.Streams._

class StreamsSuite extends CatsEffectSuite {
  val n = 10
  test(s"streaming $n elements (happy path)") {
    val stream         = printing(n)
    val (result, logs) = unsafeRunReturnLog(stream.compile.toList)
    val expected       = checkEffects(logs, n)
    assertEquals(result.get, expected)
  }
  test(s"streaming $n elements where the streams blows up half way") {
    val stream         = blowsHalfWay(n)
    val (result, logs) = unsafeRunReturnLog(stream.handleErrorWith(errorHandler).compile.toList)
    val expected       = checkEffects(logs, n / 2)
    assertEquals(result.get.map(_.toString), (expected :+ exception(EvilPayload)).map(_.toString))
  }

  private def checkEffects(logs: String, numExpected: Int): List[Int] = {
    val expected: List[Int] = (1 to numExpected).toList
    expected.map(decorate).foreach(x => assert(logs.contains(x)))
    expected
  }
}

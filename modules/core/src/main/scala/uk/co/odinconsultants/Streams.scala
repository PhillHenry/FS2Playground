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
import fs2.Stream

object Streams {

  /** Note in FS2, error handlers handle the error but then effectively rethrow it (raiseError)
    */
  type ErrorHandler[T] = PartialFunction[Throwable, Stream[IO, T]]

  def errorHandler: ErrorHandler[Throwable] = _ match { case t => Stream.eval(IOs.stackTrace(t)) }
  def evilErrorHandler[T]: ErrorHandler[T]  = _ match { case t => Stream.eval(IOs.evil(t.asInstanceOf[T])) }

  import IOs._

  val EvilPayload: Int        = -1
  val blowUp: Stream[IO, Int] = Stream.eval(evil(EvilPayload))

  def blowsHalfWay(n: Int): Stream[IO, Int] = blowsUpAfter(n / 2) ++ printing(n / 2, (n / 2) + 1)

  def blowsUpAfter(n: Int): Stream[IO, Int] =
    printing(n) ++ blowUp

  def decorate[T](x: T): String = s"x = $x"

  def printAndReturn[T](x: T): T = {
    println(decorate(x))
    x
  }

  def sleepAndPrint(n: Int, sleepMs: Long): Stream[IO, Int] = printing(n).map { i =>
    sleep(sleepMs)
    i
  }

  def printing(n: Int, start: Int = 1): Stream[IO, Int] =
    Stream.emits(start to start + n - 1).map(printAndReturn)
}

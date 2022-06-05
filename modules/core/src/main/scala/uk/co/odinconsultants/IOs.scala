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

object IOs extends EffectsOutput {

  val helloWorld: IO[String] = IO {
    val msg = "Hello, world"
    println(msg)
    msg
  }

  def printOut[T](x: T): IO[T] = IO {
    println(s"printOut: $x")
    x
  }

  def stackTrace(t: Throwable): IO[Throwable] = IO {
    println(s"stackTrace: ${t.getMessage}")
    t.printStackTrace()
    t
  }

  def evil[T](payload: T): IO[T] = IO {
    throw exception(payload)
  }

  def exception[T](payload: T): Exception = {
    new Exception(exceptionMessage(payload))
  }

  def exceptionMessage[T](payload: T): String = {
    s"Exception wrapping $payload"
  }

  val timeMs: IO[Long] = IO { System.currentTimeMillis() }

  val blockingSleep1s: IO[Unit] = IO {
    sleep(1000)
  }

  def sleep(ms: Long): Unit = {
    println(s"Sleeping for $ms ms...")
    try {
      Thread.sleep(ms)
    } catch {
      case x: InterruptedException => println(s"Interrupted ${x.getMessage}")
    }
  }
}

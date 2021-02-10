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
import cats.effect.unsafe.implicits.global

import java.io.{ByteArrayOutputStream, PrintStream}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object Runner {

  /** Execution context that runs everthing synchronously. This can be useful for testing. */
  object synchronous extends ExecutionContext {
    def execute(runnable: Runnable): Unit     = runnable.run()
    def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
  }

  def unsafeRunAndLog[T](x: IO[T]): Unit = {
    printing { (printStream, out) =>
      System.setOut(printStream)
      System.setErr(printStream)
      tryUnsafeRunAndLog(x)
      println(s"Output:\n${stringFrom(out)}")
    }
  }

  def tryUnsafeRunAndLog[T](x: IO[T]): Unit = Try {
    x.unsafeRunSync()
  } match {
    case Success(x) => println(s"Success. Result was:\n$x")
    case Failure(e) =>
      printing { (printStream, out) =>
        e.printStackTrace(printStream)
        println(s"Failed. Exception was:\n${stringFrom(out)}")
      }
  }

  private def stringFrom(out: ByteArrayOutputStream) = {
    new String(out.toByteArray)
  }

  def printing(p: (PrintStream, ByteArrayOutputStream) => Unit): Unit = {
    val out         = new ByteArrayOutputStream()
    val printStream = new PrintStream(out)
    p(printStream, out)
    printStream.close()
  }
}

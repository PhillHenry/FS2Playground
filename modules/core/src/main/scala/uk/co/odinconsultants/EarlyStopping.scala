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

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}

/**
 * SystemFw — 6/1/22 4:30 PM
you can also use Stream which gives you other options such as takeWhile or takeThrough
 */
object EarlyStopping extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    def a = IO.println("a").as(true)
    def b = IO.println("b").as(true)
    def c = IO.println("c").as(false)
    def d = IO.println("d").as(false)
    def e = IO.println("e").as(true)

    def shortCircuitOnFalse(a: IO[Boolean]*): IO[Unit] =
      a.toList
        .traverse(_.map(_.guard[Option]).nested)
        .value
        .void

    shortCircuitOnFalse(a, b, c, d, e).as(ExitCode.Success)
  }
}

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

import cats.effect.{ExitCode, IO, IOApp, Resource}
import uk.co.odinconsultants.IOs._

object ResourceErrorsMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val unreleasable: Resource[IO, String] = Resource.make(helloWorld)(x => evil(s"Won't release '$x'").void)
    val useUnreleasable: IO[String]        = unreleasable.use(x => printOut(s"use '$x'"))
    val onError: IO[String]                = useUnreleasable.onError(x => stackTrace(x).void)
    val handled: IO[String]                = onError.handleErrorWith(x => stackTrace(x).map(_.getMessage))
    (handled *> printOut("That's all folks!")).as(ExitCode.Success)
  }
}

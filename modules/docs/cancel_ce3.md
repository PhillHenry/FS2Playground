# FS2 version 3

> Daniel Spiewak @djspiewak 
> 
> So I would do the above using fs2, but if you want to do it purely 
> within Cats Effect, it would look something like this 
> (using CE3 syntax because it's more in my head than CE2 at this 
> point):

```scala mdoc
import cats.implicits._
import cats.effect.IO
import scala.concurrent.duration._
import cats.effect.unsafe.implicits.global

val server3 = {
  val counter = (0 until 5).toList traverse_ { count =>
    IO { println(s"running $count") }
  }

  counter.timeout(2.seconds)
}

server3.unsafeRunSync()
```
Note the `Uncancellable` in the `toString` rendering.

Now, let's `start` a fibre that `sleep`s and cancel it immediately.

```scala mdoc
import cats.effect.{ExitCode, IO}
import uk.co.odinconsultants.IOs
import cats.effect.unsafe.implicits.global

val cancellingSleep = for {
      _           <- IOs.helloWorld
      beforeStart <- IOs.timeMs
      f           <- IOs.blockingSleep1s.start
      afterStart  <- IOs.timeMs
      _           <- f.cancel
      afterCancel <- IOs.timeMs
      _           <- IOs.printOut(s"start took ${afterStart - beforeStart} ms, cancel took ${afterCancel - afterStart} ms.")
    } yield ExitCode.Success

cancellingSleep.unsafeRunSync()
```
Note that it is the `cancel` call that semantically blocks (and there is no `Thread.interrupt`).

> Fabio Labella @SystemFw
>
> yeah, assume it is in full (there are some bugs in ce2, but it's in full in ce3)
> the important point is that cancel doesn't return until the thing has actually canceled, rather than returning 
> immediately and then you need to explicitly put machinery in place to wait until cancellation finishes
# FS2 version 3

Daniel Spiewak @djspiewak Feb 07 20:03
So I would do the above using fs2, but if you want to do it purely 
within Cats Effect, it would look something like this 
(using CE3 syntax because it's more in my head than CE2 at this 
point):

```scala mdoc
import cats.implicits._
import cats.effect.IO
import scala.concurrent.duration._

val server3 = {
  val counter = (0 until 5).toList traverse_ { count =>
    IO.println(s"running $count")
  }

  counter.timeout(2.seconds)
}
```
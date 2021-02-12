# Effect Fairytales



## Streams

One day, there was a bad-tempered `Stream` who always blew up after a certain number:
```scala mdoc
import uk.co.odinconsultants.Runner._
import uk.co.odinconsultants.Streams._

unsafeRunAndLog(
  blowsHalfWay(10)
)
```
Unfortunately, there was no `onError` on the `Stream`. 

A handsome `handleErrorWith` could stop
an Exception being thrown but, the `Stream` would still only half complete:
```scala mdoc
unsafeRunAndLog(
  blowsHalfWay(10).map(_.toString).handleErrorWith(errorHandler.andThen(_.map(_.getMessage)))
)
```
Even if it `attempt`ed to process, the `Stream` would not throw an Exception but would stop half way:
```scala mdoc
unsafeRunAndLog(
  blowsHalfWay(10).attempt
)
```
So, the `Stream` died.

## Resource

One day, there was a happy effect who was very `Resource`ful but an evil
witch would not release her: 

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._
import uk.co.odinconsultants.Resources._
import cats.effect.{IO, Resource}

val unreleasable    = Resource.make(helloWorld)(x => evil(s"Won't release '$x'").void)
val useUnreleasable = unreleasable.use(x => printOut(s"use '$x'"))

unsafeRunAndLog( 
  useUnreleasable
)
```

When along came a handsome `onError`:
```scala mdoc
unsafeRunAndLog( 
    useUnreleasable.onError(x => stackTrace(x).void)
)
```
However, he was not able to stop the nasty witch from throwing the exception.

Instead, an even more handsome `handleErrorWith` came to the rescue:
```scala mdoc
unsafeRunAndLog( 
    useUnreleasable.handleErrorWith(x => stackTrace(x).map(_.getMessage))
)
```
and was successful in handling the witch's `Exception`.

## Guarantees

Once upon a time, there was a happy effect:

```scala mdoc
unsafeRunAndLog(evil("I'll get you, my pretty!"))
```

The evil effect was guaranteed with the happy effect:
```scala mdoc
val guaranteeBarfs = helloWorld.guarantee(evil("first guarantee"))
unsafeRunAndLog(guaranteeBarfs)

```
But nothing bad happened and the happy effect made it to the end.

Just to be sure, another happy effect guaranteed the happy effect:

```scala mdoc
val firstGuaranteeBarfs = helloWorld.guarantee(evil("first guarantee")).guarantee(printOut("second guarantee").void) 
unsafeRunAndLog(firstGuaranteeBarfs)

```
And the happy effect still made it to the end.

So, the evil effect swapped positions:
```scala mdoc
val secondGuaranteeBarfs = helloWorld.guarantee(printOut("second guarantee").void).guarantee(evil("first guarantee")) 
unsafeRunAndLog(secondGuaranteeBarfs)
```
But the happy effect still made it to the end.
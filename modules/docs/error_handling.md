# Effect Fairytales

## Resource

One day, there was a happy effect who was very `Resource`ful but an evil
witch would not release her:

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._
import uk.co.odinconsultants.Resources._
import cats.effect.{IO, Resource}

unsafeRunAndLog( 
  Resource.make(helloWorld)(x => evil(s"Won't release '$x'").void).use(printOut)
)
```

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
val firstGuaranteeBarfs = helloWorld.guarantee(evil("first guarantee")).guarantee(printOut("second guarantee")) 
unsafeRunAndLog(firstGuaranteeBarfs)

```
And the happy effect still made it to the end.

So, the evil effect swapped positions:
```scala mdoc
val secondGuaranteeBarfs = helloWorld.guarantee(printOut("second guarantee")).guarantee(evil("first guarantee")) 
unsafeRunAndLog(secondGuaranteeBarfs)
```
But the happy effect still made it to the end.
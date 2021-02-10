# Guarantees

Once upon a time, there was a happy effect:

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._
 
unsafeRunAndLog(helloWorld)
```

and an evil effect:

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._
 
unsafeRunAndLog(evil("first guarantee"))
```

The evil effect was guaranteed with the happy effect:
```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._ 

val guaranteeBarfs = helloWorld.guarantee(evil("first guarantee"))
unsafeRunAndLog(guaranteeBarfs)

```
But nothing bad happened and the happy effect made it to the end.

Just to be sure, another happy effect guaranteed the happy effect:

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._

val firstGuaranteeBarfs = helloWorld.guarantee(evil("first guarantee")).guarantee(printOut("second guarantee")) 
unsafeRunAndLog(firstGuaranteeBarfs)

```
And the happy effect still made it to the end.

So, the evil effect swapped positions:
```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._

val secondGuaranteeBarfs = helloWorld.guarantee(printOut("second guarantee")).guarantee(evil("first guarantee")) 
unsafeRunAndLog(secondGuaranteeBarfs)
```
But the happy effect still made it to the end.
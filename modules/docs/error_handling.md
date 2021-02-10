This is the story of an evil effect.

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._
 
unsafeRunAndLog(evil("first guarantee").as("Success!"))
```

Guarantee that fails:
```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._ 

val guaranteeBarfs = helloWorld.guarantee(evil("first guarantee")).as("Success!") 
unsafeRunAndLog(guaranteeBarfs)

```

First guarantee fails, but the second is OK.

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._

val firstGuaranteeBarfs = helloWorld.guarantee(evil("first guarantee")).guarantee(printOut("second guarantee")).as("Success") 
unsafeRunAndLog(firstGuaranteeBarfs)

```

Second guarantee fails, but the first is OK.

```scala mdoc
import uk.co.odinconsultants.IOs._
import uk.co.odinconsultants.Runner._

val secondGuaranteeBarfs = helloWorld.guarantee(printOut("second guarantee")).guarantee(evil("first guarantee")).as("Success") 
unsafeRunAndLog(secondGuaranteeBarfs)

```

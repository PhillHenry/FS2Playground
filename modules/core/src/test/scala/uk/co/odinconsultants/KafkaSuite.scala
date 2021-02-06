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

import munit.CatsEffectSuite
import net.manub.embeddedkafka.EmbeddedKafkaConfig
import net.manub.embeddedkafka.EmbeddedKafka._

import java.util.UUID
import scala.concurrent.duration.{DurationInt, FiniteDuration}

/** Large swathes stolen from fs2.kafka.BaseKafkaSpec
  */
class KafkaSuite extends CatsEffectSuite {

  final val transactionTimeoutInterval: FiniteDuration = 1.second

  private[this] def nextTopicName(): String =
    s"topic-${UUID.randomUUID()}"

  final def withKafka[A](
      props: Map[String, String],
      f: (EmbeddedKafkaConfig, String) => A
  ): A =
    withRunningKafkaOnFoundPort(
      EmbeddedKafkaConfig(
        customBrokerProperties = Map(
          "transaction.state.log.replication.factor" -> "1",
          "transaction.abort.timed.out.transaction.cleanup.interval.ms" -> transactionTimeoutInterval.toMillis.toString
        ) ++ props
      )
    )(f(_, nextTopicName()))
}

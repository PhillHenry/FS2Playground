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

import cats.effect.Sync
import fs2.kafka.{ConsumerSettings, KafkaDeserializer, KafkaSerializer}
import munit.CatsEffectSuite
import net.manub.embeddedkafka.EmbeddedKafka._
import net.manub.embeddedkafka.EmbeddedKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerConfig

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

  final def consumerProperties(
      config: EmbeddedKafkaConfig
  ): Map[String, String] =
    Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> s"localhost:${config.kafkaPort}",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.GROUP_ID_CONFIG -> "group"
    )

  final def consumerSettings[F[_]](
      config: EmbeddedKafkaConfig
  )(implicit F: Sync[F]): ConsumerSettings[F, String, String] =
    ConsumerSettings[F, String, String]
      .withProperties(consumerProperties(config))
      .withRecordMetadata(_.timestamp.toString)

  implicit final val stringDeserializer: KafkaDeserializer[String] =
    new org.apache.kafka.common.serialization.StringDeserializer

  implicit final val stringSerializer: KafkaSerializer[String] =
    new org.apache.kafka.common.serialization.StringSerializer

  test("KafkaConsumer#stream should consume all records with subscribe") {
    withKafka(
      Map.empty,
      { (config, topic) =>
        createCustomTopic(topic, partitions = 3)
        val produced = (0 until 5).map(n => s"key-$n" -> s"value->$n")
        publishToKafka(topic, produced)

//      val consumed =
//        KafkaConsumer.stream[IO]
//          .using(consumerSettings(config))
//          .evalTap(_.subscribeTo(topic))
//          .evalTap(consumer => IO(assert(consumer.toString.startsWith("KafkaConsumer$"))).void)
//          .evalMap(IO.sleep(3.seconds).as) // sleep a bit to trigger potential race condition with _.stream
//          .flatMap(_.stream)
//          .map(committable => committable.record.key -> committable.record.value)
//          .interruptAfter(10.seconds) // wait some time to catch potentially duplicated records
//          .compile
//          .toVector
//          .unsafeRunSync
//
//      assertEquals(consumed.toSet, produced.toSet)
      }
    )
  }
}

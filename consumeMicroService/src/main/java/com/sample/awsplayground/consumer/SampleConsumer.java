/*
 * Copyright (c) 2016-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.awsplayground.consumer;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.sample.awsplayground.model.Message;
//import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.GlobalOpenTelemetry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

//import io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context;
//import io.opentelemetry.javaagent.shaded.io.opentelemetry.context.propagation.TextMapPropagator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import com.sample.awsplayground.service.*;

/**
 * Sample consumer application using Reactive API for Kafka. To run sample
 * consumer
 * <ol>
 * <<<<<<< HEAD
 * <li>Start Zookeeper and Kafka server
 * <li>Update {@link #BOOTSTRAP_SERVERS} and {@link #TOPIC} if required
 * <li>Create Kafka topic {@link #TOPIC}
 * <li>Send some messages to the topic, e.g. by running
 * <li>Run {@link SampleConsumer} as Java application with all dependent jars in
 * the CLASSPATH (eg. from IDE).
 * <li>Shutdown Kafka server and Zookeeper when no longer required =======
 * <li>Start Zookeeper and Kafka server
 * <li>Update {@link #BOOTSTRAP_SERVERS} and {@link #TOPIC} if required
 * <li>Create Kafka topic {@link #TOPIC}
 * <li>Send some messages to the topic, e.g. by running {@link SampleProducer}
 * <li>Run {@link SampleConsumer} as Java application with all dependent jars in
 * the CLASSPATH (eg. from IDE).
 * <li>Shutdown Kafka server and Zookeeper when no longer required >>>>>>>
 * 051008f3f6318144856dc551a0a589ed8d7df6e8
 * </ol>
 */
@Component
public class SampleConsumer {

    private final Logger log = LoggerFactory.getLogger(SampleConsumer.class.getName());

    private final String BOOTSTRAP_SERVERS = "localhost:9092";
    private final String TOPIC = "reactive-kafka-topic";

    private ReceiverOptions<Integer, String> receiverOptions;
    private final SimpleDateFormat dateFormat;

    @Autowired
    ConsumeService consumeService;

    private final WebClient webClient;

    public SampleConsumer() {
        String bootstrapServers = BOOTSTRAP_SERVERS;
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "reactive-kafka-client");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-kafka-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        receiverOptions = ReceiverOptions.create(props);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
        this.webClient = WebClient.builder().baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    public Disposable consumeMessages(String topic, CountDownLatch latch) {

        ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
        Flux<ReceiverRecord<Integer, String>> kafkaFlux = KafkaReceiver.create(options).receive();


        return kafkaFlux.subscribe(record -> {
            ReceiverOffset offset = record.receiverOffset();

            String message = record.value();
            String topic_name = offset.topicPartition().toString();
            String offset_val = String.valueOf(offset.offset());

            System.out.printf("Received message: topic-partition=%s offset=%s timestamp=%s value=%s\n", topic_name,
                    offset_val, dateFormat.format(new Date(record.timestamp())), message);
            Mono<Message> cap = addToDB(message, offset_val, topic_name);
            Flux<String> api = callWebClient();
            api.subscribe(System.out::println);

            // Processing completed. Acknowledge the offset
            offset.acknowledge();
            latch.countDown();
        });
    }

    public void sendMessages() throws Exception {
        int count = 20;
        CountDownLatch latch = new CountDownLatch(count);
        Disposable disposable = consumeMessages(TOPIC, latch);

        latch.await(10, TimeUnit.SECONDS);
        disposable.dispose();
    }

    public Mono<Message> addToDB(String msg, String offset, String topic) {
        Message message = new Message(msg.concat(offset), msg, offset, topic);
        return consumeService.saveConsume(message);
    }

    public Flux<String> callWebClient() {
        return this.webClient.get().uri("/posts").retrieve().bodyToFlux(String.class);
    }

}
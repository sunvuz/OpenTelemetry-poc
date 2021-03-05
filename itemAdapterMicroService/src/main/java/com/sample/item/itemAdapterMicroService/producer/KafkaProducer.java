package com.sample.item.itemAdapterMicroService.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class.getName());
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "reactive-kafka-topic";
    private static final String CLIENT_ID_CONFIG = "reactive-kafka-client";

    private final KafkaSender<String, String> sender;
    private final SimpleDateFormat dateFormat;

    public KafkaProducer(String bootstrapServers){

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);

        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");

    }

    public void close() {
        try {
        sender.close();
        } catch (Exception ex) {
            log.debug("Exception occurred while closing the kafaSender -- check later");
        }
    }

    public void generateMessages(String topic, int count, CountDownLatch latch) throws InterruptedException {
        sender.<Integer>send(Flux.range(1, count)
                .map(i -> SenderRecord.create(new ProducerRecord<>(topic, "Key_"+i, "Message_" + i), i)))
                .doOnError(e -> log.error("Send failed", e))
                .subscribe(r -> {
                    RecordMetadata metadata = r.recordMetadata();
                    System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s%n",
                            r.correlationMetadata(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            dateFormat.format(new Date(metadata.timestamp())));
                    latch.countDown();
                });
    }

    public void publishMessages() throws InterruptedException {
        int count = 2;
        CountDownLatch latch = new CountDownLatch(count);
        KafkaProducer producer = new KafkaProducer(BOOTSTRAP_SERVERS);
        producer.generateMessages(TOPIC, count, latch);
        latch.await(10, TimeUnit.SECONDS);
        producer.close();
    }
}
package com.sample.cart.shoppingMicroService.controller;

import com.sample.cart.shoppingMicroService.service.ItemService;
import com.sample.cart.shoppingMicroService.model.Item;
import com.sample.cart.shoppingMicroService.producer.KafkaProducer;
import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.shaded.io.opentelemetry.context.propagation.TextMapPropagator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("shop")
public class ItemController {

    @Autowired
    ItemService itemService;

    @PostConstruct
    public void saveItem() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(123, "Headphone", "$200.00"));
        items.add(new Item(124, "Pendrive", "$50.00"));
        items.add(new Item(355, "Keyboard", "$30.00"));
        itemService.initializeEmployees(items);
    }

    @GetMapping
    public Flux<Item> getitems(@RequestHeader Map<String, String> headers) throws InterruptedException {
        //Add dummy transaction ID to MDC, to check if the data is accessible from externally included Aspect
        // Aspect included in POM : com.helix.observability - aspect
        MDC.put("transactionId","trans-id-12345");
        //https://aws-otel.github.io/docs/getting-started/java-sdk/trace-manual-instr
        Span downStreamSpan = Span.current();
        String uuid = UUID.randomUUID().toString();
        downStreamSpan.setAttribute("TRANSACTION_ID", uuid);
        downStreamSpan.setAttribute("accountId", "1234");


        headers.forEach((key, value) -> {
            System.out.println(String.format("Header '%s' = %s", key, value));
        });
        //Insert into Cassandra DB
        List<Item> saveItems = new ArrayList<>();
        saveItems.add(new Item(654, "Mouse", "$40.00"));
        itemService.initializeEmployees(saveItems);
        //Call Reative Kafka Producer
        KafkaProducer kafkaProducer = new KafkaProducer("localhost:9092");
        kafkaProducer.publishMessages();
        //Call Adapter Service using WebClient

        return itemService.callItemAdapterService();
    }


    public TextMapPropagator.Getter<Map<String, String>> getter = new TextMapPropagator.Getter<Map<String, String>>() {
        @Override
        public String get(Map<String, String> headers, String key) {

            if( headers.size() > 0) {
                return headers.get(key);
            }
            return null;
        }

        @Override
        public Iterable<String> keys(Map<String, String> headers) {
            List<String> keySet = new ArrayList<String>();

            headers.entrySet().forEach(entry -> {
                keySet.add(entry.getKey());
            });
            return keySet;
        }
    };
}
package com.sample.item.itemAdapterMicroService.controller;

import com.sample.item.itemAdapterMicroService.model.Item;
import com.sample.item.itemAdapterMicroService.producer.KafkaProducer;
import com.sample.item.itemAdapterMicroService.service.ItemAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("itemadapter")
public class ItemAdapterController {

    @Autowired
    ItemAdapterService itemAdapterService;

    @PostConstruct
    public void saveItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(123, "Headphone", "$200.00"));
        items.add(new Item(124, "Pendrive", "$50.00"));
        items.add(new Item(355, "Keyboard", "$30.00"));
        itemAdapterService.initializeEmployees(items);
    }

    @GetMapping("/list")
    public Flux<Item> getAllItems() throws InterruptedException {
        Flux<Item> items = itemAdapterService.getAllItems();
        // Call Reative Kafka Producer
        KafkaProducer kafkaProducer = new KafkaProducer("localhost:9092");
        kafkaProducer.publishMessages();

        return items;
    }

    @GetMapping("/captureflag")
    public Mono<Boolean> captureFlag() throws InterruptedException {
        return Mono.just(true);
    }
}
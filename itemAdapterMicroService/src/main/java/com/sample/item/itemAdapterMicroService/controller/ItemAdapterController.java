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


    @GetMapping("/list")
    public Flux<Item> getAllItems() throws InterruptedException {
        Flux<Item> items = itemAdapterService.getAllItems();
        return items;
    }

}
package com.sample.item.itemAdapterMicroService.service;

import com.sample.item.itemAdapterMicroService.repository.*;
import com.sample.item.itemAdapterMicroService.model.Item;
import com.sample.item.itemAdapterMicroService.repository.ItemAdapterRepository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.sample.item.itemAdapterMicroService.model.*;

@Service
public class ItemAdapterService {

    @Autowired
    ItemAdapterRepository itemAdapterRepository;

    private final WebClient webClient;

	public ItemAdapterService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:9091").build();
    }
    
    public void initializeEmployees(List<Item> items) {
        Flux<Item> savedItems = itemAdapterRepository.saveAll(items);
        savedItems.subscribe();
    }

    public Flux<Item> getAllItems() {
        Flux<Item> items =  itemAdapterRepository.findAll();
        return items;
    }

    public Flux<Item> callItemAdapterService() {
		return this.webClient.get().uri("/itemadapter/list")
						.retrieve().bodyToFlux(Item.class);
	}
}
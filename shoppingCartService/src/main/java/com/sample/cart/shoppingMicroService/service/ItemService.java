package com.sample.cart.shoppingMicroService.service;

import com.sample.cart.shoppingMicroService.model.Item;
import com.sample.cart.shoppingMicroService.repository.ItemRepository;
import com.sample.cart.shoppingMicroService.repository.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.sample.cart.shoppingMicroService.model.*;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    private final WebClient webClient;

	public ItemService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:9091").build();
    }
    
    public void initializeEmployees(List<Item> items) {
        Flux<Item> savedItems = itemRepository.saveAll(items);
        savedItems.subscribe();
    }

    public Flux<Item> getAllItems() {
        Flux<Item> items =  itemRepository.findAll();
        return items;
    }

    public Flux<Item> callItemAdapterService() {
		return this.webClient.get().uri("/itemadapter/list")
						.retrieve().bodyToFlux(Item.class);
	}
}
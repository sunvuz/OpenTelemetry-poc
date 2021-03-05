package com.sample.cart.shoppingMicroService.repository;

import com.sample.cart.shoppingMicroService.model.Item;
import com.sample.cart.shoppingMicroService.model.*;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface ItemRepository extends ReactiveCassandraRepository<Item, Integer> {


}
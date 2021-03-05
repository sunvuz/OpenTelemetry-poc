package com.sample.item.itemAdapterMicroService.repository;


import com.sample.item.itemAdapterMicroService.model.Item;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface ItemAdapterRepository extends ReactiveCassandraRepository<Item, Integer> {


}
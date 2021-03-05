package com.sample.awsplayground.repository;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import com.sample.awsplayground.model.Message;

public interface MessageRepository extends ReactiveCassandraRepository<Message, Integer> {
}



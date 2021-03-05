package com.sample.awsplayground.service;

import com.sample.awsplayground.repository.*;
import com.sample.awsplayground.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConsumeService {

    @Autowired
    MessageRepository messageRepository;

    public  Mono<Message> saveConsume(Message message) {
        Mono<Message> savedCon = messageRepository.save(message);
        return savedCon;
    }

    public Flux<Message> getAllAuths() {
        Flux<Message> consumed=  messageRepository.findAll();
        return consumed;
    }

}

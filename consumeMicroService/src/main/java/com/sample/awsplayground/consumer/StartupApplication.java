package com.sample.awsplayground.consumer;

import com.sample.awsplayground.consumer.SampleConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplication  implements ApplicationRunner {

    @Autowired
    SampleConsumer sampleConsumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        while(true) {
            sampleConsumer.sendMessages();
        }
    }

}

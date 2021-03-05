package com.sample.awsplayground.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Table
@Data
@NoArgsConstructor
public class Message {
    @PrimaryKey
    private String msgoffset;
    private String message;
    private String offset;
    private String topic;

    public Message(String msgoffset, String message, String offset, String topic) {
        this.msgoffset = msgoffset;
        this.message = message;
        this.offset = offset;
        this.topic = topic;
    }


}

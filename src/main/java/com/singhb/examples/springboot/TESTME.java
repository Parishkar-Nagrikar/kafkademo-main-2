package com.singhb.examples.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

public class TESTME {

    @Autowired
    @Qualifier("kafkaTemplateBoot")
    private KafkaTemplate<String, String> template;

    public void main(String[] args) {
        this.template.send("mytopic","bla","bladata");

    }
}


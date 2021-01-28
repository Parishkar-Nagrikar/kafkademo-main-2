package com.singhb.examples.springboot.web.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {
	public static Logger logger = LoggerFactory.getLogger(DemoRestController.class);
	@Autowired
	@Qualifier("kafkaTemplateBoot")
	private KafkaTemplate<String, String> template;


	@GetMapping("/producer/{message}")
	public String sendMessage(@PathVariable(name = "message") String message) {

		try {
			send(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Done Sending-->" + message;
	}

	@Transactional
	public void send(String message) throws InterruptedException {
		this.template.send("myTopic", System.currentTimeMillis() + "", message);
	}

	// , Acknowledgment ack
	@KafkaListener(topics = "myTopic", containerFactory = "kafkaListenerContainerFactory")
	public void listen(KafkaConsumer<String,String> consumer,ConsumerRecord<String,String> cr, String data , @Header(KafkaHeaders.OFFSET) int offset) throws Exception {
		logger.info("offset for message is ----> " +  data  + "**** " + offset);

		RebalanceListner rebalanceListner  = new RebalanceListner(consumer);

		if (data.equals("rrr")) {
			logger.info("There was an exception -->" + data);
			throw new Exception("Error for data");
		} else {
			logger.info("Received -->" + data);
			rebalanceListner.addOffset(cr.topic(), cr.partition(), cr.offset());
			consumer.commitSync(rebalanceListner.getCurrentOffsets());
		}
	}
}

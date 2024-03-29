package com.rbkmoney.deanonimus;


import com.rbkmoney.deanonimus.extension.ElasticsearchContainerExtension;
import com.rbkmoney.deanonimus.extension.KafkaContainerExtension;
import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Map;

@Slf4j
@ExtendWith({KafkaContainerExtension.class, ElasticsearchContainerExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final String TOPIC_NAME = "mg-events-party";

    @DynamicPropertySource
    static void containersProps(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KafkaContainerExtension.KAFKA::getBootstrapServers);
        registry.add("spring.elasticsearch.rest.uris",
                ElasticsearchContainerExtension.ELASTIC_SEARCH::getHttpHostAddress);
    }

    public static void sendMessages(List<SinkEvent> sinkEvents) {
        final Producer<String, SinkEvent> producer = createProducer();
        sinkEvents.forEach(sinkEvent ->
                producer.send(new ProducerRecord<>(TOPIC_NAME, sinkEvent.getEvent().getSourceId(), sinkEvent)));
    }

    private static Producer<String, SinkEvent> createProducer() {
        final Map<String, Object> configs =
                KafkaTestUtils.producerProps(KafkaContainerExtension.KAFKA.getBootstrapServers());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        return new KafkaProducer<>(configs);
    }

}

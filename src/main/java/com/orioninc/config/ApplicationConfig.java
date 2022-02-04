package com.orioninc.config;

import com.orioninc.ex.FailedDeserializationProvider;
import com.orioninc.models.User;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.orioninc")
@EnableKafka
@PropertySource("classpath:application.properties")
public class ApplicationConfig {
    @Value("${kafka.server}")
    String kafkaServer;

    @Value("${topics.first}")
    String topicsFirst;

    @Value("${topics.second}")
    String topicsSecond;

    @Bean
    public NewTopic firstTopic() {
        return TopicBuilder.name(topicsFirst)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic secondTopic() {
        return TopicBuilder.name(topicsSecond)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);

        KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);
        kafkaAdmin.createOrModifyTopics(firstTopic(), secondTopic());

        return kafkaAdmin;
    }

    @Bean
    public ConsumerFactory<String, User> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);

        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.orioninc.models.User");

        config.put(ErrorHandlingDeserializer.VALUE_FUNCTION, FailedDeserializationProvider.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> jsonUsersKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, User> producerFactoryJson() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactoryString());
        kafkaTemplate.setDefaultTopic(topicsFirst);

        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, User> kafkaTemplateJson() {
        KafkaTemplate<String, User> kafkaTemplate = new KafkaTemplate<>(producerFactoryJson());
        kafkaTemplate.setDefaultTopic(topicsSecond);

        return kafkaTemplate;
    }
}

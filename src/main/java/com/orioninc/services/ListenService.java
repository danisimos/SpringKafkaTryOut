package com.orioninc.services;

import com.orioninc.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class ListenService {
    @Autowired
    KafkaTemplate<String, User> kafkaTemplateJson;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplateString;

    public String send(String key, String value) {
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplateString.sendDefault(key, value);
        System.out.println("done");

        listenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failed to send to " + kafkaTemplateString.getDefaultTopic());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("sent to " + kafkaTemplateString.getDefaultTopic());
            }
        });

        return value;
    }

    @KafkaListener(topics = "#{'${topics.first}'}", groupId = "json", containerFactory = "jsonUsersKafkaListenerContainerFactory")
    public void listenJsonUsers(User user,
                                @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName) {
        System.out.println("received from: " + topicName + user);
        user.setHandledTimestamp(timestamp);

        ListenableFuture<SendResult<String, User>> listenableFuture = kafkaTemplateJson.sendDefault(user);
        listenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Failed to send");
            }

            @Override
            public void onSuccess(SendResult<String, User> result) {
                System.out.println("Success sent to \"topic2\"");
            }
        });
    }

}

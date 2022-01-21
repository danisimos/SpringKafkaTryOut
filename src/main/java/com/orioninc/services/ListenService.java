package com.orioninc.services;

import com.orioninc.models.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class ListenService {
    @KafkaListener(topics = "topic1", groupId = "jsonUsersGroup", containerFactory = "jsonUsersKafkaListenerContainerFactory")
    @SendTo("topic2")
    public User listenJsonUsers(User user, @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        user.setHandledTimestamp(timestamp);
        return user;
    }

    @KafkaListener(topics = "topic1", groupId = "stringUsersGroup")
    public void listenStringUsers(String data) {
        System.out.println("listen string " + data);
    }
}

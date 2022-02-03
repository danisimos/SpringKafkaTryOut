package com.orioninc.ex;

import com.orioninc.models.User;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

public class FailedDeserializationProvider implements Function<FailedDeserializationInfo, User> {
    @Override
    public User apply(FailedDeserializationInfo failedDeserializationInfo) {
        System.out.println("deserialization failed");
        return new User();
    }
}

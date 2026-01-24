package com.example.graduation_project.kafka;

import java.util.List;
import java.util.Map;

public record EventChangeMessage(
        Long eventId,
        Long changerId,
        Long ownerId,
        Map<String, FieldChange> changedFields,
        List<Long> subscriberIds
)  {

}
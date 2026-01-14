package com.example.graduation_project.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class EventStatusSchedulerUpdater {

    private static final Logger log = LoggerFactory.getLogger(EventStatusSchedulerUpdater.class);

    private final EventRepository eventRepository;

    public EventStatusSchedulerUpdater(
            EventRepository eventRepository
    ) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.status.cron}")
    @Transactional
    public void updateEventsStatuses() {
        log.info("EventStatusScheduledUpdater started");
        LocalDateTime now = LocalDateTime.now();
        // STARTED
        eventRepository.changeEventsAsStarted(now);

        // FINISHED
        List<EventsEntity> startedEvents =
                eventRepository.findByStatus(EventStatus.STARTED);

        List<LocalDateTime> endTimes = startedEvents.stream()
                                                    .filter(e -> e.getDate()
                                                                  .plusMinutes(e.getDuration())
                                                                  .isBefore(now))
                                                    .map(EventsEntity::getDate)
                                                    .toList();

        endTimes.forEach(eventRepository::changeEventsAsFinished);
    }
}

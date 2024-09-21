package ru.practicum.event.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.request.dto.ParticipationRequestDto;


import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private static final Logger log = LoggerFactory.getLogger(PrivateEventController.class);
    private final EventService privateEventService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable int userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Начало метода @GetMapping getEvents");
        return privateEventService.getEvents(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto createEvent(@PathVariable int userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Начало метода @PostMapping createEvent");
        return privateEventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Начало метода @GetMapping getEvent");
        return privateEventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int userId, @PathVariable int eventId,
                                    @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("Начало метода @PatchMapping updateEvent");
        return privateEventService.updateEvent(userId, eventId, updateEventRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Начало метода @GetMapping getRequestsForEvent");
        return privateEventService.getRequestForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestForEvent(@PathVariable int userId,
                                                                      @PathVariable int eventId,
                                                                      @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                              statusUpdateRequest) {
        log.info("Начало метода @PatchMapping updateStatusRequestForEvent");
        return privateEventService.updateStatusRequestForEvent(userId, eventId, statusUpdateRequest);
    }
}

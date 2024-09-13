package ru.practicum.event.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService privateEventService;

    @GetMapping
    public List<EventFullDto> getEvents(@PathVariable int userId,
                                        @RequestParam(required = false, defaultValue = "0") int from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {

        return privateEventService.getEvents(userId, from, size);
    }

    @PostMapping
    public EventFullDto createEvent(@PathVariable int userId,
                                    @RequestBody NewEventDto newEventDto) {

        return privateEventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable int userId, @PathVariable int eventId) {

        return privateEventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int userId, @PathVariable int eventId,
                                    @RequestBody UpdateEventUserRequest updateEventUserRequest) {

        return privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable int userId, @PathVariable int eventId) {

        return privateEventService.getRequestForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestForEvent(@PathVariable int userId,
                                                                      @PathVariable int eventId,
                                                                      @RequestBody EventRequestStatusUpdateRequest
                                                                              statusUpdateRequest) {
        return null;
    }
}

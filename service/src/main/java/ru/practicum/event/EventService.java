package ru.practicum.event;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEvents(int userId, int from, int size);

    EventFullDto createEvent(int userId, NewEventDto newEventDto);

    EventFullDto getEvent(int userId, int eventId);

    EventFullDto updateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestForEvent(int userId, int eventId);

    EventRequestStatusUpdateResult updateStatusRequestForEvent(int userId, int eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest);
}

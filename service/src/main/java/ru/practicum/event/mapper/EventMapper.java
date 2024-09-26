package ru.practicum.event.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event, Map<Integer, Long> viewsMap,
                                              Map<Integer, List<CommentDto>> comments) {

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .createdOn(DateTimeAdapter.toString(event.getCreatedOn()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .views(viewsMap != null && viewsMap.containsKey(event.getId())
                        ? viewsMap.get(event.getId())
                        : 0L)
                .publishedOn(event.getPublishedOn() != null
                        ? DateTimeAdapter.toString(event.getPublishedOn())
                        : null)
                .comments(comments != null && comments.containsKey(event.getId())
                        ? comments.get(event.getId())
                        : List.of())
                .build();
    }

    public static EventShortDto mapToEventShortDto(Event event, Map<Integer, Long> viewsMap,
                                                   Map<Integer, List<CommentDto>> comments) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(viewsMap != null && viewsMap.containsKey(event.getId())
                        ? viewsMap.get(event.getId())
                        : 0L)
                .comments(comments != null && comments.containsKey(event.getId())
                        ? comments.get(event.getId())
                        : List.of())
                .build();
    }

    public static Event adminUpdateEvent(Event event, UpdateEventRequest updateEvent, Category category,
                                         State state, Location location) {
        return updateEvent(event, updateEvent, category, state, location);
    }

    public static Event privateUpdateEvent(Event event, UpdateEventRequest updateEvent, Category category, State state) {
        return updateEvent(event, updateEvent, category, state, null);
    }

    public static Event createToEvent(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .description(newEventDto.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(newEventDto.getEventDate()))
                .initiator(initiator)
                .location(LocationMapper.mapLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid() != null
                        ? newEventDto.getPaid()
                        : false)
                .participantLimit(newEventDto.getParticipantLimit() != null
                        ? newEventDto.getParticipantLimit()
                        : 0)
                .createdOn(LocalDateTime.now())
                .requestModeration(newEventDto.getRequestModeration() != null
                        ? newEventDto.getRequestModeration()
                        : true)
                .state(State.PENDING)
                .build();
    }

    private static Event updateEvent(Event event, UpdateEventRequest updateEvent, Category category, State state,
                                     Location location) {
        return event.toBuilder()
                .annotation(updateEvent.getAnnotation() != null
                        ? updateEvent.getAnnotation()
                        : event.getAnnotation())
                .category(updateEvent.getCategory() != null
                        ? category
                        : event.getCategory())
                .description(updateEvent.getDescription() != null
                        ? updateEvent.getDescription()
                        : event.getDescription())
                .eventDate(updateEvent.getEventDate() != null
                        ? DateTimeAdapter.stringToLocalDateTime(updateEvent.getEventDate())
                        : event.getEventDate())
                .location(location != null
                        ? location
                        : event.getLocation())
                .paid(updateEvent.getPaid() != null
                        ? updateEvent.getPaid()
                        : event.getPaid())
                .participantLimit(updateEvent.getParticipantLimit() != null
                        ? updateEvent.getParticipantLimit()
                        : event.getParticipantLimit())
                .requestModeration(updateEvent.getRequestModeration() != null
                        ? updateEvent.getRequestModeration()
                        : event.getRequestModeration())
                .title(updateEvent.getTitle() != null
                        ? updateEvent.getTitle()
                        : event.getTitle())
                .state(state != null
                        ? state
                        : event.getState())
                .publishedOn(event.getPublishedOn())
                .build();
    }

}

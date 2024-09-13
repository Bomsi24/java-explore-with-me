package ru.practicum.event.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {

        return EventFullDto.builder()
                .id(event.getId())
                .tittle(event.getTittle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .views(event.getViews())
                .build();
    }

    public static Event mapToEvent(EventFullDto eventFullDto, CategoryDto categoryDto,
                                   LocationDto locationDto, UserDto userDto) {

        return Event.builder()
                .id(eventFullDto.getId())
                .tittle(eventFullDto.getTittle())
                .annotation(eventFullDto.getAnnotation())
                .category(CategoryMapper.mapCategory(categoryDto))
                .confirmedRequests(eventFullDto.getConfirmedRequests())
                .description(eventFullDto.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(eventFullDto.getEventDate()))
                .initiator(UserMapper.mapToUser(userDto))
                .location(LocationMapper.mapLocation(locationDto))
                .paid(eventFullDto.getPaid())
                .participantLimit(eventFullDto.getParticipantLimit())
                .publishedOn(DateTimeAdapter.stringToLocalDateTime(eventFullDto.getPublishedOn()))
                .requestModeration(eventFullDto.getRequestModeration())
                .state(State.valueOf(eventFullDto.getState()))
                .views(eventFullDto.getViews())
                .build();
    }

    public static Event maptoEvent(Integer id, NewEventDto newEventDto, Category category, Integer confirmedRequests,
                                   User initiator, LocalDateTime publishedOn, Integer views) {
        return Event.builder()
                .id(id)
                .tittle(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .description(newEventDto.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(newEventDto.getEventDate()))
                .initiator(initiator)
                .location(LocationMapper.mapLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(newEventDto.getRequestModeration())
                .views(views)
                .build();
    }

    public static Event maptoEvent(Integer id, UpdateEventUserRequest updateEventUserRequest, Category category,
                                   Integer confirmedRequests, User initiator,
                                   LocalDateTime publishedOn, Integer views) {
        return Event.builder()
                .id(id)
                .tittle(updateEventUserRequest.getTittle())
                .annotation(updateEventUserRequest.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .description(updateEventUserRequest.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(updateEventUserRequest.getEventDate()))
                .initiator(initiator)
                .location(LocationMapper.mapLocation(updateEventUserRequest.getLocation()))
                .paid(updateEventUserRequest.getPaid())
                .participantLimit(updateEventUserRequest.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(updateEventUserRequest.getRequestModeration())
                .views(views)
                .build();
    }

    public static EventShortDto mapToEventShortDto(Event event) {

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .tittle(event.getTittle())
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> mapToEventShortDtoList(Set<Event> events) {

        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }
}

package ru.practicum.event.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event, Map<Integer, Long> viewsMap) {

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
                .views(viewsMap != null && viewsMap.containsKey(event.getId()) ?
                        viewsMap.get(event.getId()) : 0L)
                .publishedOn(event.getPublishedOn() != null ? DateTimeAdapter.toString(event.getPublishedOn()) : null)
                .build();
    }

    public static Event mapToEvent(EventFullDto eventFullDto, CategoryDto categoryDto,
                                   LocationDto locationDto, UserDto userDto) {

        return Event.builder()
                .id(eventFullDto.getId())
                .title(eventFullDto.getTitle())
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
                .build();
    }

    public static Event maptoEvent(Integer id, NewEventDto newEventDto, Category category, Integer confirmedRequests,
                                   User initiator, LocalDateTime createdOn) {
        return Event.builder()
                .id(id)
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .description(newEventDto.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(newEventDto.getEventDate()))
                .initiator(initiator)
                .location(LocationMapper.mapLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .createdOn(createdOn)
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    public static EventShortDto mapToEventShortDto(Event event, Map<Integer, Long> viewsMap) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(viewsMap != null && viewsMap.containsKey(event.getId()) ?
                        viewsMap.get(event.getId()) : 0L)
                .build();
    }

   /* public static List<EventShortDto> mapToEventShortDtoList(Set<Event> events) {

        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }*/

    public static EventRequestDto mapToEventRequestDto(Event event) {

        return EventRequestDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(DateTimeAdapter.toString(event.getEventDate()))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState().name())
                .createdOn(DateTimeAdapter.toString(event.getCreatedOn()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .requestModeration(event.getRequestModeration())
                .build();
    }
}

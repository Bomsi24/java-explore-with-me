package ru.practicum.event.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;

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
                .views(viewsMap != null && viewsMap.containsKey(event.getId())
                        ? viewsMap.get(event.getId())
                        : 0L)
                .publishedOn(event.getPublishedOn() != null
                        ? DateTimeAdapter.toString(event.getPublishedOn())
                        : null)
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
                .views(viewsMap != null && viewsMap.containsKey(event.getId())
                        ? viewsMap.get(event.getId())
                        : 0L)
                .build();
    }
}

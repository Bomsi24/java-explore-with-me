package ru.practicum.request.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto mapToParticipationRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(DateTimeAdapter.toString(request.getCreatedTime()))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();

    }
}

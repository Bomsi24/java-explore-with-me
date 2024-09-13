package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateEventUserRequest {
    private String annotation;
    private Integer category;
    private String description;
    private String eventDate; //Новые дата и время на которые намечено событие
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String tittle;
}

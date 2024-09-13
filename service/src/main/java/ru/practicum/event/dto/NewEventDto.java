package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {
    private String annotation;
    private Integer category;
    private String description;
    private String eventDate; // Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
}

/*
private Category category;
private Integer confirmedRequests;
private User initiator;
LocalDateTime publishedOn;
private Integer views;*/

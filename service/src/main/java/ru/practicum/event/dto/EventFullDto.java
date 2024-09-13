package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    private Integer id;
    private String tittle;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String description;
    private String eventDate;//Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    private Boolean requestModeration;
    private String state;
    private Integer views;
}

package ru.practicum.event.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    @NotNull
    @NotEmpty
    private String annotation;

    @NotNull
    private CategoryDto category;

    @NotNull
    @NotEmpty
    private String eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    @NotEmpty
    private LocationDto location;

    @NotNull
    @NotEmpty
    private Boolean paid;

    @NotNull
    @NotEmpty
    private String title;

    @Min(value = 0)
    private Integer participantLimit;

    private List<CommentDto> comments;
    private String createdOn;
    private Integer id;
    private Integer confirmedRequests;
    private String description;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private Long views;
}

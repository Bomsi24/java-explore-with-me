package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    private Integer id;
    private String created;//Время создания
    private Integer event;
    private Integer requester;
    private String status;
}

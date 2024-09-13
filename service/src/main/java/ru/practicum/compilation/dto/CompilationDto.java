package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompilationDto {
    private int id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String tittle;
}

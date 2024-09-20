package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

import java.util.Map;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents() != null
                        ? compilation.getEvents().stream()
                        .map(event -> EventMapper.mapToEventShortDto(event, Map.of())).toList() : null)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}

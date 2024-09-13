package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.mapToEventShortDtoList(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .build();
    }
}

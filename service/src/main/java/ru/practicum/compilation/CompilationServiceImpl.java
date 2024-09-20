package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDto createCompilation(NewCompilationDto newCompilation) {
        log.info("Начало работы метода createCompilation");
        Set<Event> events = new HashSet<>();
        if (newCompilation.getEvents() != null && !newCompilation.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(newCompilation.getEvents());
        }

        Compilation compilationBuilder = Compilation.builder()
                .events(events)
                .pinned(newCompilation.getPinned() != null ? newCompilation.getPinned() : false)
                .title(newCompilation.getTitle())
                .build();

        Compilation compilation = compilationRepository.save(compilationBuilder);

        return CompilationMapper.toDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilation) {
        log.info("Начало работы метода updateCompilation");
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка не найдена", ""));

        if (updateCompilation.getEvents() != null && !updateCompilation.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());
            oldCompilation.setEvents(new HashSet<>(events));
        }

        if (updateCompilation.getPinned() != null) {
            oldCompilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getTitle() != null) {
            oldCompilation.setTitle(updateCompilation.getTitle());
        }

        Compilation newCompilation = compilationRepository.save(oldCompilation);

        return CompilationMapper.toDto(newCompilation);
    }

    @Override
    public void deleteCompilation(int compId) {
        log.info("Начало работы метода deleteCompilation");
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка не найдена", ""));

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Начало работы метода getCompilations");
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Compilation> page = compilationRepository.findByPinned(pinned, pageable);

        return page.stream()
                .map(CompilationMapper::toDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilation(int compId) {
        log.info("Начало работы метода getCompilation");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ValidationException("Compilation with id=" + compId + " was not found",
                        "The required object was not found."));

        return CompilationMapper.toDto(compilation);
    }
}

package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(UpdateCompilationRequest updateCompilation) {

        Set<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());
        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();

        Compilation compilation = Compilation.builder()
                .events(events)
                .pinned(updateCompilation.getPinned())
                .tittle(updateCompilation.getTittle())
                .build();

        Compilation newCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toDto(newCompilation);
    }

    @Override
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilation) {
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow();

        if (!updateCompilation.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findByIdIn(updateCompilation.getEvents());
            oldCompilation.getEvents().addAll(events);
        }

        oldCompilation.setPinned(updateCompilation.getPinned());
        oldCompilation.setTittle(updateCompilation.getTittle());
        Compilation newCompilation = compilationRepository.save(oldCompilation);

        return CompilationMapper.toDto(compilationRepository.save(newCompilation));
    }

    @Override
    public void deleteCompilation(int compId) {

    }
}

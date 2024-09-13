package ru.practicum.compilation;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto createCompilation(UpdateCompilationRequest updateCompilation);

    CompilationDto updateCompilation(int compId, UpdateCompilationRequest updateCompilation);

    void deleteCompilation(int compId);

}

package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    public final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody UpdateCompilationRequest updateCompilation) {
        return compilationService.createCompilation(updateCompilation);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable int compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable int compId,
                                            @RequestBody UpdateCompilationRequest updateCompilation) {
        return compilationService.updateCompilation(compId,updateCompilation);
    }
}

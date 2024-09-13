package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateCompilationRequest {
    private List<Integer> events;
    private Boolean pinned;
    private String tittle;
}

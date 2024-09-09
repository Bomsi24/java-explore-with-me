package ru.practicum.stats;

import java.util.List;

public interface ElementStatsService {
    List<ElementStatsResponseDto> getStatsFromService(String start, String end, List<String> uris, boolean unique);

    void saveHit(ElementStatsSaveDto statsSaveDto);
}

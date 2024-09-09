package ru.practicum.stats.mapper;

import ru.practicum.stats.ElementStatsResponseDto;
import ru.practicum.stats.ElementStatsSaveDto;
import ru.practicum.stats.model.ElementStats;

public class ElementStatsMapper {

    public static ElementStatsResponseDto mapToStatsDto(ElementStats stats, Long hits) {
        return ElementStatsResponseDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(hits)
                .build();
    }

    public static ElementStats mapToStats(ElementStatsSaveDto statsSaveDto) {
        return ElementStats.builder()
                .app(statsSaveDto.getApp())
                .uri(statsSaveDto.getUri())
                .ip(statsSaveDto.getIp())
                .createdDate(statsSaveDto.getCreatedDate())
                .build();
    }
}

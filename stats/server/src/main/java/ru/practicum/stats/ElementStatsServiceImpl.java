package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.stats.mapper.ElementStatsMapper;
import ru.practicum.stats.model.ElementStats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ElementStatsServiceImpl implements ElementStatsService {
    private final ElementStatsRepository statsRepository;

    @Override
    public List<ElementStatsResponseDto> getStatsFromService(String start, String end,
                                                             List<String> uris, boolean unique) {
        log.info("Начало работы метода getStatsFromService");

        LocalDateTime startTime = DateTimeAdapter.stringToLocalDateTime(
                URLDecoder.decode(start, StandardCharsets.UTF_8));
        LocalDateTime endTime = DateTimeAdapter.stringToLocalDateTime(
                URLDecoder.decode(end, StandardCharsets.UTF_8));

        List<ElementStats> stats = fetchStats(startTime, endTime, uris, unique);
        return mapToResponseDto(stats);
    }

    //Получение статистики из БД
    private List<ElementStats> fetchStats(LocalDateTime startTime, LocalDateTime endTime,
                                          List<String> uris, boolean unique) {
        boolean hasUris = (uris != null && !uris.isEmpty());

        if (unique) {
            if (hasUris) {
                return statsRepository.getStatsOriginalIp(startTime, endTime, uris);
            } else {
                return statsRepository.getStatsOriginalIp(startTime, endTime);
            }
        } else {
            if (hasUris) {
                return statsRepository.getStatsNotOriginalIp(startTime, endTime, uris);
            } else {
                return statsRepository.getStatsNotOriginalIp(startTime, endTime);
            }
        }
    }

    //Метод для преобразования ответа в ElementStatsResponseDto
    private List<ElementStatsResponseDto> mapToResponseDto(List<ElementStats> stats) {
        Map<String, Long> statsCount = countStatsByUri(stats);

        return statsCount.entrySet().stream()
                .map(entry -> createResponseDto(entry.getKey(), entry.getValue(), stats))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(ElementStatsResponseDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    //получаем мапу из URI и количесва просмотров этого URI
    private Map<String, Long> countStatsByUri(List<ElementStats> stats) {
        return stats.stream()
                .collect(Collectors.groupingBy(ElementStats::getUri, Collectors.counting()));
    }

    //Метод для преобразования каждого отдельного элемента в ElementStatsResponseDto
    private ElementStatsResponseDto createResponseDto(String uri, Long count, List<ElementStats> stats) {
        ElementStats elementStats = stats.stream()
                .filter(e -> e.getUri().equals(uri))
                .findFirst()
                .orElse(null);
        return ElementStatsMapper.mapToStatsDto(elementStats, count);
    }

    @Override
    public void saveHit(ElementStatsSaveDto statsSaveDto) {
        log.info("Начало работы saveHit");
        log.info("statsSaveDto: {}", statsSaveDto);
        statsSaveDto.setCreatedDate(LocalDateTime.now());
        ElementStats newElementStats = ElementStatsMapper.mapToStats(statsSaveDto);
        statsRepository.save(newElementStats);
        log.info("Хит сохранен в БД");
    }
}

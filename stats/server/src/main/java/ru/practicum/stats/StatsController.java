package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ElementStatsResponseDto> getStatsFromService(
            @Valid @RequestParam String start,
            @Valid @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique
    ) {
        log.info("Начало выполнение эндпоинта GET /stats");
        return statsService.getStatsFromService(start, end, uris, unique);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void saveHit(@Valid @RequestBody ElementStatsSaveDto statsSaveDto) {
        log.info("Начало выполнение эндпоинта POST /hit");
        statsService.saveHit(statsSaveDto);
    }
}

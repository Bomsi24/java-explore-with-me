package ru.practicum.request.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class PrivateRequestController {

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequestForUser(@PathVariable int userId) {
        return null;
    }

    @PostMapping("/requests")
    public ParticipationRequestDto createRequestForUser(@PathVariable int userId, @RequestParam int eventId) {
        return null;
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable int userId, @PathVariable int requestId) {
        return null;
    }
}
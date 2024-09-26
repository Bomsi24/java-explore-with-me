package ru.practicum.comment.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentStatusUpdate;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping
    public List<CommentDto> updateCommentStatus(@PathVariable int eventId,
                                                @Valid @RequestBody CommentStatusUpdate commentUpdateStatus) {
        log.info("Начало работы эндпоинта @PatchMapping updateCommentStatus");
        return commentService.updateCommentStatus(eventId, commentUpdateStatus);
    }
}

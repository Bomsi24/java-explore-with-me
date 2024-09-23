package ru.practicum.comment.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.CommentDto;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable int userId, @PathVariable int eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Начало работы эндпоинта @PostMapping createComment");
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable int userId, @PathVariable int eventId,
                                    @PathVariable int commentId,
                                    @Valid @RequestBody NewCommentDto updateComment) {
        log.info("Начало работы эндпоинта @PatchMapping updateComment");
        return commentService.updateComment(userId, eventId, commentId, updateComment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable int userId, @PathVariable int eventId, @PathVariable int commentId) {
        log.info("Начало работы эндпоинта @DeleteMapping deleteComment");
        commentService.deleteComment(userId, eventId, commentId);
    }
}

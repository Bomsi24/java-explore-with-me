package ru.practicum.comment;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.comment.dto.CommentStatusUpdate;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(int userId, int eventId, NewCommentDto comment);

    CommentDto updateComment(int userId, int eventId, int commentId, NewCommentDto updateComment);

    void deleteComment(int userId, @PathVariable int eventId, int commentId);

    List<CommentDto> updateCommentStatus(int eventId, CommentStatusUpdate commentStatusUpdate);
}

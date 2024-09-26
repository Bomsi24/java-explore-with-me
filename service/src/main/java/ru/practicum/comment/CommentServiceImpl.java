package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentStatusUpdate;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto createComment(int userId, int eventId, NewCommentDto newComment) {
        User user = checkAndReturnUser(userId);
        Event event = checkAndReturnEvent(eventId);

        Comment comment = CommentMapper.createComment(newComment, user, event);
        Comment createComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(createComment);
    }

    @Override
    public CommentDto updateComment(int userId, int eventId, int commentId, NewCommentDto updateComment) {
        User user = checkAndReturnUser(userId);
        Event event = checkAndReturnEvent(eventId);
        Comment comment = CommentMapper.updateComment(updateComment, user, event, commentId);

        commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void deleteComment(int userId, int eventId, int commentId) {
        checkAndReturnUser(userId);
        checkAndReturnEvent(eventId);
        commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Коммента нет", ""));

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> updateCommentStatus(int eventId,
                                                CommentStatusUpdate commentStatusUpdate) {
        checkAndReturnEvent(eventId);

        List<Comment> comments = commentRepository.findByIdIn(commentStatusUpdate.getCommentsId());
        State status = State.valueOf(commentStatusUpdate.getStatus());

        for (Comment comment : comments) {
            if (comment.getModerationStatus() != State.PENDING) {
                throw new ConflictException("Статус должен быть PENDING", "");
            } else {
                comment.setModerationStatus(status);
            }
        }

        List<Comment> newComments = commentRepository.saveAll(comments);

        return newComments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    private User checkAndReturnUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь c id:" + userId + " отсутствует", ""));
    }

    private Event checkAndReturnEvent(int eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Ивента c id:" + eventId + " нет", ""));
    }
}

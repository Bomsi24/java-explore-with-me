package ru.practicum.comment.mapper;

import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment createComment(NewCommentDto newCommentDto, User user, Event event) {

        return Comment.builder()
                .creator(user)
                .event(event)
                .text(newCommentDto.getText())
                .creationDate(LocalDateTime.now())
                .moderationStatus(State.PENDING)
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment) {

        return CommentDto.builder()
                .id(comment.getId())
                .event(comment.getEvent().getId())
                .creator(comment.getCreator().getId())
                .text(comment.getText())
                .moderationStatus(comment.getModerationStatus().name())
                .createdDate(DateTimeAdapter.toString(comment.getCreationDate()))
                .build();
    }

    public static Comment updateComment(NewCommentDto comment, User user, Event event, int commentId) {

        return Comment.builder()
                .id(commentId)
                .text(comment.getText())
                .event(event)
                .creator(user)
                .moderationStatus(State.PENDING)
                .creationDate(LocalDateTime.now())
                .build();
    }
}

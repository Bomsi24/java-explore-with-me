package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventFullDto> getEvents(int userId, int from, int size) {

        findUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> categoryPage = eventRepository.findByInitiatorId(userId, pageable);

        return categoryPage.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto createEvent(int userId, NewEventDto newEventDto) {

        User initiator = findUserById(userId);
        if (isCheckinEventTime(newEventDto.getEventDate())) {
            return null;//вернуть ошибку по времени
        }

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        Event newEvent = EventMapper.maptoEvent(
                null,
                newEventDto,
                category,
                0,
                initiator,
                LocalDateTime.now(),
                0);

        newEvent.setState(State.PENDING);
        Event createEvent = eventRepository.save(newEvent);

        return EventMapper.toEventFullDto(createEvent);
    }

    @Override
    public EventFullDto getEvent(int userId, int eventId) {

        findUserById(userId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest) {

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (event.getState() == State.PUBLISHED) {
            return null;//ошибка 409
        }
        if (isCheckinEventTime(updateEventUserRequest.getEventDate())) {
            return null;//ошибка во времени
        }

        Event updatedEvent = EventMapper.maptoEvent(
                eventId,
                updateEventUserRequest,
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getInitiator(),
                event.getPublishedOn(),
                event.getViews());

        Event newEvent = eventRepository.save(updatedEvent);

        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestForEvent(int userId, int eventId) {

        List<Request> request = requestRepository.findByRequesterIdAndEventId(userId, eventId);

        if (request.isEmpty()) {
            return null;
        }

        return request.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatusRequestForEvent(int userId, int eventId,
                                                                      EventRequestStatusUpdateRequest
                                                                              statusUpdateRequest) {

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        if (event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return null;//модерация не нужна
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            return null;//больше подовать завявок нельзя
        }
        if (event.getState() != State.PENDING) {
            return null;//можно поменять только если в состоянии ожидания
        }

        List<Request> requests = requestRepository.findByRequesterId(userId);
        List<Integer> requestIds = requests.stream()
                .map(Request::getId)
                .filter(id -> !statusUpdateRequest.getRequestIds().contains(id))
                .toList();
        //доработаь метод


        return null;
    }

    private Event finEventById(int eventId) {
        return eventRepository.findById(eventId).orElseThrow();
    }

    private User findUserById(int userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    private boolean isCheckinEventTime(String date) {
        LocalDateTime currentTime = LocalDateTime.now().plusHours(2);
        LocalDateTime eventDate = DateTimeAdapter.stringToLocalDateTime(date);
        return eventDate.isBefore(currentTime);
    }
}

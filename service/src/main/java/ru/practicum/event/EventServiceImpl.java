package ru.practicum.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.adapter.DateTimeAdapter;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.stats.ElementStatsResponseDto;
import ru.practicum.stats.StatsClient;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.practicum.event.model.QEvent.event;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final String thisService = "ewm-main-service";
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventShortDto> getEvents(int userId, int from, int size) {
        log.info("Начало работы метода getEvents");
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        log.info("Получение страницы ивентов");
        Page<Event> categoryPage = eventRepository.findByInitiatorId(userId, pageable);
        Map<Integer, Long> viewsMap = getViews(categoryPage.getContent());

        return categoryPage.getContent().stream()
                .map(event -> EventMapper.mapToEventShortDto(event, viewsMap))
                .toList();
    }

    @Override
    public EventFullDto createEvent(int userId, NewEventDto newEventDto) {
        log.info("Начало работы метода createEvent");
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Юзера нет", ""));

        if (isCheckinEventTime(newEventDto.getEventDate())) {
            throw new ValidationException("Неверная дата", "");
        }

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Категории нет", ""));

        locationRepository.save(LocationMapper.mapLocation(newEventDto.getLocation()));

        Event newEvent = createToEvent(newEventDto, category, initiator);
        Event createEvent = eventRepository.save(newEvent);

        Map<Integer, Long> viewsMap = getViews(List.of(createEvent));

        return EventMapper.toEventFullDto(createEvent, viewsMap);
    }

    @Override
    public EventFullDto getEvent(int userId, int eventId) {
        log.info("Начало работы метода getEvent");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("События нет", ""));

        Map<Integer, Long> viewsMap = getViews(List.of(event));

        return EventMapper.toEventFullDto(event, viewsMap);
    }

    @Override
    public EventFullDto updateEvent(int userId, int eventId, UpdateEventRequest updateEventRequest) {
        log.info("Начало работы метода updateEvent");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("События нет", ""));

        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Событие находится в опубликованном состоянии", "");
        }

        if (updateEventRequest.getEventDate() != null && isCheckinEventTime(updateEventRequest.getEventDate())) {
            throw new ValidationException("Неверное время", "");
        }

        Category category = null;
        if (updateEventRequest.getCategory() != null) {
            category = categoryRepository.findById(updateEventRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Категории нет", ""));
        }

        State state = null;
        if (updateEventRequest.getStateAction() != null) {
            state = updateEventRequest.getStateAction().equalsIgnoreCase("CANCEL_REVIEW") ? State.CANCELED : State.PENDING;
        }

        Event updatedEvent = privateUpdateEvent(event, updateEventRequest, category, state);
        Event savedEvent = eventRepository.save(updatedEvent);

        Map<Integer, Long> viewsMap = getViews(List.of(savedEvent));

        return EventMapper.toEventFullDto(savedEvent, viewsMap);
    }

    @Override
    public List<ParticipationRequestDto> getRequestForEvent(int userId, int eventId) {
        log.info("Начало работы метода getRequestForEvent");
        List<Request> request = requestRepository.findByEventId(eventId);

        return request.stream()
                .map(RequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestForEvent(int userId, int eventId,
                                                                      EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Начало работы метода updateStatusRequestForEvent");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие не найдено", ""));

        validateParticipantLimit(event);

        RequestStatus requestStatus = RequestStatus.valueOf(statusUpdateRequest.getStatus());
        List<Request> requests = requestRepository.findByIdIn(statusUpdateRequest.getRequestIds());

        List<Request> updatedRequests = new ArrayList<>();
        List<Request> noUpdateRequests = new ArrayList<>();

        for (Request request : requests) {
            processRequest(request, requestStatus, event, updatedRequests, noUpdateRequests);
        }

        requestRepository.saveAll(updatedRequests);
        requestRepository.saveAll(noUpdateRequests);
        event.setConfirmedRequests(event.getConfirmedRequests() + updatedRequests.size());
        eventRepository.save(event);

        return buildUpdateResult(updatedRequests, noUpdateRequests);
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Integer> users,
                                             List<String> states,
                                             List<Integer> categories,
                                             String rangeStart,
                                             String rangeEnd,
                                             int from, int size) {
        log.info("Начало работы метода getAdminEvents");
        QEvent event = QEvent.event;

        BooleanExpression predicate = event.isNotNull();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        if (users != null && !users.isEmpty()) {
            predicate = predicate.and(event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            List<State> stateList = states.stream()
                    .map(State::valueOf)
                    .toList();
            predicate = predicate.and(event.state.in(stateList));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories));
        }

        predicate = addDateRangePredicate(predicate, rangeStart, rangeEnd);

        List<Event> events = queryFactory.selectFrom(event)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Map<Integer, Long> viewsMap = getViews(events);
        return events.stream()
                .map(eventStream -> EventMapper.toEventFullDto(eventStream, viewsMap))
                .toList();
    }

    @Override
    public EventFullDto patchAdminEvent(int eventId, UpdateEventRequest updateEvent) {
        log.info("Начало работы метода patchAdminEvent");
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие не найдено", ""));

        validateEventDate(updateEvent, event);
        State state = determineState(updateEvent, event);

        Category category = (updateEvent.getCategory() != null)
                ? categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() ->
                new NotFoundException("Категории нет", ""))
                : null;

        Location location = (updateEvent.getLocation() != null)
                ? locationRepository.save(LocationMapper.mapLocation(updateEvent.getLocation()))
                : null;

        Event updatedEvent = adminUpdateEvent(event, updateEvent, category, state, location);
        Event newEvent = eventRepository.save(updatedEvent);
        Map<Integer, Long> viewsMap = getViews(List.of(newEvent));

        return EventMapper.toEventFullDto(newEvent, viewsMap);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Integer> categories,
                                               Boolean paid,
                                               String rangeStart,
                                               String rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {
        log.info("Начало работы метода getPublicEvents");

        QEvent event = QEvent.event;

        BooleanExpression predicate = event.isNotNull();

        if (text != null && !text.isEmpty()) {
            predicate = predicate.and(event.annotation.containsIgnoreCase(text)
                    .or(event.description.containsIgnoreCase(text)));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories));
        }

        if (paid != null) {
            predicate = predicate.and(event.paid.eq(paid));
        }

        predicate = addDateRangePredicate(predicate, rangeStart, rangeEnd);

        if (onlyAvailable != null && onlyAvailable) {
            predicate = predicate.and(event.participantLimit.gt(event.confirmedRequests));
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = queryFactory.select(event)
                .from(event)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        statsClient.saveHit(thisService, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        Map<Integer, Long> viewsMpa = getViews(events);

        List<EventShortDto> eventShortDto = events.stream()
                .map(eventStream -> EventMapper.mapToEventShortDto(eventStream, viewsMpa))
                .toList();

        List<EventShortDto> eventsSort;
        if ("EVENT_DATE".equals(sort)) {
            eventsSort = eventShortDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .toList();

        } else {
            eventsSort = eventShortDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }

        return eventsSort;
    }

    @Override
    public EventFullDto getPublicEvent(int id, HttpServletRequest request) {
        log.info("Начало работы метода getPublicEvent");
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("События нет", ""));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие не опубликованно", "");
        }

        statsClient.saveHit(thisService, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        Map<Integer, Long> viewsMpa = getViews(List.of(event));

        return EventMapper.toEventFullDto(event, viewsMpa);
    }

    private void validateParticipantLimit(Event event) {
        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();
        log.info("participantLimit: {}", participantLimit);
        log.info("confirmedRequests: {}", confirmedRequests);

        if (participantLimit > 0 && participantLimit <= confirmedRequests) {
            throw new ConflictException("Больше создать заявок нельзя", "");
        }
    }

    private void processRequest(Request request, RequestStatus requestStatus, Event event,
                                List<Request> updatedRequests, List<Request> noUpdateRequests) {

        if (requestStatus != RequestStatus.CONFIRMED) {
            handleCancellation(request, noUpdateRequests);
        } else {
            handleConfirmation(request, event, updatedRequests, noUpdateRequests);
        }
    }

    private void handleCancellation(Request request, List<Request> noUpdateRequests) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Заявка не в состоянии ожидания", "");
        }
        request.setStatus(RequestStatus.CANCELED);
        noUpdateRequests.add(request);
    }

    private void handleConfirmation(Request request, Event event, List<Request> updatedRequests, List<Request> noUpdateRequests) {
        int confirmedRequests = event.getConfirmedRequests();
        int participantLimit = event.getParticipantLimit();

        if (participantLimit > confirmedRequests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Заявка не в состоянии ожидания", "");
            }

            request.setStatus(RequestStatus.CONFIRMED);
            updatedRequests.add(request);

        } else {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Заявка не в состоянии ожидания", "");
            }

            request.setStatus(RequestStatus.REJECTED);
            noUpdateRequests.add(request);
        }
    }

    private EventRequestStatusUpdateResult buildUpdateResult(List<Request> updatedRequests, List<Request> noUpdateRequests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(RequestMapper.mapToParticipationRequestDtoList(updatedRequests))
                .rejectedRequests(RequestMapper.mapToParticipationRequestDtoList(noUpdateRequests))
                .build();
    }


    private void validateEventDate(UpdateEventRequest updateEvent, Event event) {
        if (updateEvent.getEventDate() != null) {
            LocalDateTime eventDate = DateTimeAdapter.stringToLocalDateTime(updateEvent.getEventDate());
            log.info("eventDate {}", eventDate);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата не должна быть раньше нынешнего времени", "");
            }
        }
    }

    private State determineState(UpdateEventRequest updateEvent, Event event) {
        if (updateEvent.getStateAction() != null) {
            StateAction stateAction = StateAction.valueOf(updateEvent.getStateAction());
            log.info("state {}", stateAction);
            if (stateAction == StateAction.PUBLISH_EVENT && event.getState() != State.PENDING) {
                throw new ConflictException("Событие не в ожидании публикации", "");
            }
            if (stateAction == StateAction.REJECT_EVENT && event.getState() == State.PUBLISHED) {
                throw new ConflictException("Событие уже опубликованно", "");
            }
            return stateAction == StateAction.PUBLISH_EVENT ? State.PUBLISHED : State.CANCELED;
        }
        return null;
    }

    private BooleanExpression addDateRangePredicate(BooleanExpression predicate, String rangeStart, String rangeEnd) {
        LocalDateTime startDate = null;
        if (rangeStart != null) {
            startDate = DateTimeAdapter.stringToLocalDateTime(rangeStart);
            predicate = predicate.and(event.eventDate.goe(startDate));
        }

        LocalDateTime endDate = null;
        if (rangeEnd != null) {
            endDate = DateTimeAdapter.stringToLocalDateTime(rangeEnd);
            predicate = predicate.and(event.eventDate.loe(endDate));
        }

        if ((startDate != null && endDate != null) && startDate.isAfter(endDate)) {
            throw new ValidationException("Неверная дата", "");
        }

        return predicate;
    }

    private Event adminUpdateEvent(Event event, UpdateEventRequest updateEvent, Category category,
                                   State state, Location location) {
        return updateEvent(event, updateEvent, category, state, location);
    }

    private Event privateUpdateEvent(Event event, UpdateEventRequest updateEvent, Category category, State state) {
        return updateEvent(event, updateEvent, category, state, null);
    }

    private Event updateEvent(Event event, UpdateEventRequest updateEvent, Category category, State state, Location location) {
        return event.toBuilder()
                .annotation(updateEvent.getAnnotation() != null ? updateEvent.getAnnotation() : event.getAnnotation())
                .category(updateEvent.getCategory() != null ? category : event.getCategory())
                .description(updateEvent.getDescription() != null ? updateEvent.getDescription() : event.getDescription())
                .eventDate(updateEvent.getEventDate() != null ? DateTimeAdapter.stringToLocalDateTime(updateEvent.getEventDate()) : event.getEventDate())
                .location(location != null ? location : event.getLocation())
                .paid(updateEvent.getPaid() != null ? updateEvent.getPaid() : event.getPaid())
                .participantLimit(updateEvent.getParticipantLimit() != null ? updateEvent.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(updateEvent.getRequestModeration() != null ? updateEvent.getRequestModeration() : event.getRequestModeration())
                .title(updateEvent.getTitle() != null ? updateEvent.getTitle() : event.getTitle())
                .state(state != null ? state : event.getState())
                .publishedOn(event.getPublishedOn())
                .build();
    }

    private boolean isCheckinEventTime(String date) {
        LocalDateTime currentTime = LocalDateTime.now().plusHours(2);
        LocalDateTime eventDate = DateTimeAdapter.stringToLocalDateTime(date);
        return eventDate.isBefore(currentTime);
    }

    private Event createToEvent(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .description(newEventDto.getDescription())
                .eventDate(DateTimeAdapter.stringToLocalDateTime(newEventDto.getEventDate()))
                .initiator(initiator)
                .location(LocationMapper.mapLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit() != null
                        ? newEventDto.getParticipantLimit() : 0)
                .createdOn(LocalDateTime.now())
                .requestModeration(newEventDto.getRequestModeration() != null
                        ? newEventDto.getRequestModeration() : true)
                .state(State.PENDING)
                .build();
    }

    private Map<Integer, Long> getViews(List<Event> events) {
        List<String> eventId = events.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .toList();
        log.info("eventId {}", eventId);

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        List<ElementStatsResponseDto> stats = statsClient.getStats(start, LocalDateTime.now(), eventId, true);

        log.info("Stats: {}", stats.toString());
        return stats.stream()
                .filter(stat -> stat.getUri() != null)
                .map(stat -> {
                    String uri = stat.getUri();
                    log.info("uri {}", uri);

                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(uri);
                    int id = 0;
                    if (matcher.find()) {
                        id = Integer.parseInt(matcher.group());
                    }

                    return Map.entry(id, stat.getHits());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }
}

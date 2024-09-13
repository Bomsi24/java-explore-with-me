package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findByInitiatorId(Integer initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Integer initiatorId, Integer id);

    Set<Event> findByIdIn(List<Integer> ids);
}

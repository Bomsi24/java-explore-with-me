package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequesterIdAndEventId(Integer requesterId, Integer eventId);

    //List<Request> findByIdIn(List<Integer> requesterIds);

    List<Request> findByRequesterId(Integer requesterId);
}

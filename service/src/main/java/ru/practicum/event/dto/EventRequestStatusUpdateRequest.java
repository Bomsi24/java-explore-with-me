package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private RequestStatus requestStatus;
}

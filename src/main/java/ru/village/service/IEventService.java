package ru.village.service;

import java.util.List;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.controller.dto.response.EventResponse;

public interface IEventService {
    EventResponse create(CreateEventRequest req);
    List<EventResponse> findAll();
}

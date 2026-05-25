package ru.village.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.CreateEventRequest;
import ru.village.controller.dto.response.EventResponse;
import ru.village.domain.Event;
import ru.village.mapper.EventMapper;
import ru.village.repository.EventRepository;

/** CRUD событий (платёжных периодов). */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventResponse create(CreateEventRequest req) {
        Event saved = eventRepository.save(new Event(null, req.name(), req.cost()));
        log.info("audit: event created id={} name='{}' cost={} by user={}",
                saved.getId(), saved.getName(), saved.getCost(), currentUser());
        return eventMapper.toResponse(saved);
    }

    private static String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "system" : auth.getName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream().map(eventMapper::toResponse).toList();
    }
}

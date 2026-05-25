package ru.village.mapper;

import org.mapstruct.Mapper;
import ru.village.controller.dto.response.EventResponse;
import ru.village.domain.Event;

/** Event entity → EventResponse DTO. */
@Mapper(componentModel = "spring")
public interface EventMapper {
    EventResponse toResponse(Event event);
}

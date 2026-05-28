package ru.village.mapper;

import org.mapstruct.Mapper;
import ru.village.controller.dto.response.ContactResponse;
import ru.village.domain.UsefulContactInfo;

/** UsefulContactInfo entity → ContactResponse DTO. */
@Mapper(componentModel = "spring")
public interface UsefulContactInfoMapper {
    ContactResponse toResponse(UsefulContactInfo c);
}

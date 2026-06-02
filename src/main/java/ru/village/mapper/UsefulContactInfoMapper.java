package ru.village.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.village.controller.dto.response.ContactResponse;
import ru.village.domain.UsefulContactInfo;
import ru.village.util.ContactFormatter;

/** UsefulContactInfo entity → ContactResponse DTO. contactInfoHtml собирает tel:-ссылки. */
@Mapper(componentModel = "spring")
public abstract class UsefulContactInfoMapper {

    @Autowired
    protected ContactFormatter contactFormatter;

    @Mapping(target = "contactInfoHtml", expression = "java(contactFormatter.phoneLinks(c.getContactInfo()))")
    public abstract ContactResponse toResponse(UsefulContactInfo c);
}

package ru.village.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.village.controller.dto.response.ExpenseResponse;
import ru.village.domain.Expense;

/** Expense entity → ExpenseResponse DTO. */
@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "eventName", source = "event.name")
    ExpenseResponse toResponse(Expense expense);
}

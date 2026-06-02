package ru.village.controller.dto.response;

import java.util.List;

/** Детали дома: полный адрес + список жителей. */
public record HouseholdDetailDto(
        Long id,
        String address,
        List<InhabitantDto> inhabitants
) {
}

package ru.village.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.response.AddressDto;
import ru.village.repository.HouseholdRepository;

/** Поиск адресов домохозяйств по подстроке улицы. */
@Service
@RequiredArgsConstructor
public class HouseholdService implements IHouseholdService {

    private final HouseholdRepository householdRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressDto> searchAddresses(String query) {
        return householdRepository.searchByStreetSubstring(query == null ? "" : query)
                .stream()
                .map(h -> new AddressDto(
                        h.getId(),
                        "ул. " + h.getAddress().getStreet().getName()
                                + ", д. " + h.getAddress().getBldng().getNumber()))
                .toList();
    }
}

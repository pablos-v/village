package ru.village.service;

import java.util.List;
import ru.village.controller.dto.response.AddressDto;

public interface IHouseholdService {
    List<AddressDto> searchAddresses(String query);
}

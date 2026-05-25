package ru.village.service;

import java.util.List;
import ru.village.controller.dto.request.SaveContactRequest;
import ru.village.controller.dto.response.ContactResponse;

public interface IUsefulContactInfoService {
    List<ContactResponse> findAll();
    ContactResponse create(SaveContactRequest req);
    ContactResponse update(Long id, SaveContactRequest req);
    void delete(Long id);
}

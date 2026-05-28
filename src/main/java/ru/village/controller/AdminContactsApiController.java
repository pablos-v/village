package ru.village.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.village.controller.dto.request.SaveContactRequest;
import ru.village.controller.dto.response.ContactResponse;
import ru.village.service.IUsefulContactInfoService;

/** REST CRUD по контактам — только ADMIN. */
@RestController
@RequestMapping("/api/admin/contacts")
@RequiredArgsConstructor
public class AdminContactsApiController {

    private final IUsefulContactInfoService service;

    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody SaveContactRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public ContactResponse update(@PathVariable Long id, @Valid @RequestBody SaveContactRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

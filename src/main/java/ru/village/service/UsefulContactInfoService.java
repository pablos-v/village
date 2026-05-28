package ru.village.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.SaveContactRequest;
import ru.village.controller.dto.response.ContactResponse;
import ru.village.domain.UsefulContactInfo;
import ru.village.exception.EntityNotFoundException;
import ru.village.mapper.UsefulContactInfoMapper;
import ru.village.repository.UsefulContactInfoRepository;

/** CRUD по полезным контактам (врачи, квартальная, и т.д.). */
@Service
@RequiredArgsConstructor
public class UsefulContactInfoService implements IUsefulContactInfoService {

    private final UsefulContactInfoRepository repo;
    private final UsefulContactInfoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> findAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ContactResponse create(SaveContactRequest req) {
        var saved = repo.save(new UsefulContactInfo(null, req.type(), req.contactInfo(), req.comment()));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ContactResponse update(Long id, SaveContactRequest req) {
        var c = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Contact " + id + " не найден"));
        c.setType(req.type());
        c.setContactInfo(req.contactInfo());
        c.setComment(req.comment());
        return mapper.toResponse(repo.save(c));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new EntityNotFoundException("Contact " + id + " не найден");
        repo.deleteById(id);
    }
}

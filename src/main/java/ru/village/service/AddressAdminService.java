package ru.village.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.controller.dto.request.SaveAddressRequest;
import ru.village.controller.dto.request.SaveInhabitantRequest;
import ru.village.controller.dto.response.HouseholdAdminDto;
import ru.village.controller.dto.response.HouseholdDetailDto;
import ru.village.controller.dto.response.HouseholdEditDto;
import ru.village.controller.dto.response.InhabitantDto;
import ru.village.controller.dto.response.StreetDto;
import ru.village.domain.Address;
import ru.village.domain.Bldng;
import ru.village.domain.Household;
import ru.village.domain.Inhabitant;
import ru.village.domain.Street;
import ru.village.exception.AddressInUseException;
import ru.village.exception.EntityNotFoundException;
import ru.village.repository.AddressRepository;
import ru.village.repository.BldngRepository;
import ru.village.repository.HouseholdRepository;
import ru.village.repository.InhabitantRepository;
import ru.village.repository.PaymentRepository;
import ru.village.repository.StreetRepository;

/** Админ-CRUD адресов (домохозяйств) и жителей. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressAdminService implements IAddressAdminService {

    private final HouseholdRepository householdRepository;
    private final AddressRepository addressRepository;
    private final BldngRepository bldngRepository;
    private final StreetRepository streetRepository;
    private final InhabitantRepository inhabitantRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HouseholdAdminDto> list() {
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : inhabitantRepository.countByHousehold()) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        return householdRepository.findAllOrdered().stream()
                .map(h -> new HouseholdAdminDto(h.getId(), label(h), counts.getOrDefault(h.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StreetDto> streets() {
        return streetRepository.findAllByOrderByNameAsc().stream()
                .map(s -> new StreetDto(s.getId(), s.getName()))
                .toList();
    }

    @Override
    @Transactional
    public void create(SaveAddressRequest req) {
        Street street = resolveStreet(req);
        Bldng bldng = bldngRepository.save(new Bldng(null, req.number().trim(), blankToNull(req.description())));
        Address address = addressRepository.save(new Address(null, street, bldng));
        Household saved = householdRepository.save(new Household(null, address));
        log.info("audit: address created hh={} street='{}' number='{}'",
                saved.getId(), street.getName(), req.number());
    }

    @Override
    @Transactional(readOnly = true)
    public HouseholdEditDto getForEdit(Long id) {
        Household h = household(id);
        Address a = h.getAddress();
        return new HouseholdEditDto(h.getId(), a.getStreet().getId(),
                a.getBldng().getNumber(), a.getBldng().getDscrptn(), label(h));
    }

    @Override
    @Transactional
    public void update(Long id, SaveAddressRequest req) {
        Household h = household(id);
        Address address = h.getAddress();
        Bldng bldng = address.getBldng();
        Street street = resolveStreet(req);
        bldng.setNumber(req.number().trim());
        bldng.setDscrptn(blankToNull(req.description()));
        bldngRepository.save(bldng);
        address.setStreet(street);
        addressRepository.save(address);
        log.info("audit: address updated hh={} street='{}' number='{}'",
                id, street.getName(), req.number());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (paymentRepository.countByHouseholdId(id) > 0) {
            throw new AddressInUseException(
                    "По этому адресу есть поступления — удалить нельзя. Сначала измените или удалите его платежи.");
        }
        Household h = household(id);
        Long addressId = h.getAddress().getId();
        Long bldngId = h.getAddress().getBldng().getId();
        inhabitantRepository.deleteByHouseholdId(id);
        householdRepository.delete(h);
        householdRepository.flush();
        // адрес/здание чистим, только если их больше никто не использует (улицу оставляем — общая)
        if (!householdRepository.existsByAddressId(addressId)) {
            addressRepository.deleteById(addressId);
            addressRepository.flush();
            if (!addressRepository.existsByBldngId(bldngId)) {
                bldngRepository.deleteById(bldngId);
            }
        }
        log.info("audit: address deleted hh={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public HouseholdDetailDto detail(Long id) {
        Household h = household(id);
        List<InhabitantDto> inhabitants = inhabitantRepository
                .findByHouseholdIdOrderByMasterDescNameAsc(id).stream()
                .map(i -> new InhabitantDto(i.getId(), i.getName(), i.getPhone(), i.isMaster()))
                .toList();
        return new HouseholdDetailDto(h.getId(), label(h), inhabitants);
    }

    @Override
    @Transactional
    public void addInhabitant(Long householdId, SaveInhabitantRequest req) {
        Household h = household(householdId);
        inhabitantRepository.save(new Inhabitant(
                null, req.name().trim(), blankToNull(req.phone()), h, req.master()));
        log.info("audit: inhabitant added hh={} name='{}'", householdId, req.name());
    }

    @Override
    @Transactional
    public void updateInhabitant(Long inhabitantId, SaveInhabitantRequest req) {
        Inhabitant i = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new EntityNotFoundException("Inhabitant " + inhabitantId + " не найден"));
        i.setName(req.name().trim());
        i.setPhone(blankToNull(req.phone()));
        i.setMaster(req.master());
        inhabitantRepository.save(i);
        log.info("audit: inhabitant updated id={}", inhabitantId);
    }

    @Override
    @Transactional
    public void deleteInhabitant(Long inhabitantId) {
        inhabitantRepository.deleteById(inhabitantId);
        log.info("audit: inhabitant deleted id={}", inhabitantId);
    }

    /** Улица: либо существующая по id, либо новая по названию (с переиспользованием совпадающей). */
    private Street resolveStreet(SaveAddressRequest req) {
        if (req.streetId() != null) {
            return streetRepository.findById(req.streetId())
                    .orElseThrow(() -> new EntityNotFoundException("Street " + req.streetId() + " не найдена"));
        }
        String name = req.newStreet() == null ? "" : req.newStreet().trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Выберите улицу из списка или укажите новую");
        }
        return streetRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> streetRepository.save(new Street(null, name)));
    }

    private Household household(Long id) {
        return householdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Household " + id + " не найден"));
    }

    private static String label(Household h) {
        var a = h.getAddress();
        return "ул. " + a.getStreet().getName() + ", д. " + a.getBldng().getNumber();
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}

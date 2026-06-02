package ru.village.service;

import java.util.List;
import ru.village.controller.dto.request.SaveAddressRequest;
import ru.village.controller.dto.request.SaveInhabitantRequest;
import ru.village.controller.dto.response.HouseholdAdminDto;
import ru.village.controller.dto.response.HouseholdDetailDto;
import ru.village.controller.dto.response.HouseholdEditDto;
import ru.village.controller.dto.response.StreetDto;

/** Админ-CRUD адресов (домохозяйств) и жителей. */
public interface IAddressAdminService {
    List<HouseholdAdminDto> list();
    List<StreetDto> streets();
    void create(SaveAddressRequest req);
    HouseholdEditDto getForEdit(Long id);
    void update(Long id, SaveAddressRequest req);
    void delete(Long id);

    HouseholdDetailDto detail(Long id);
    void addInhabitant(Long householdId, SaveInhabitantRequest req);
    void updateInhabitant(Long inhabitantId, SaveInhabitantRequest req);
    void deleteInhabitant(Long inhabitantId);
}

package ru.village.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.village.controller.dto.request.SaveAddressRequest;
import ru.village.controller.dto.request.SaveInhabitantRequest;
import ru.village.service.IAddressAdminService;

/** Админ-страницы CRUD адресов (домохозяйств) и жителей. ADMIN-only. */
@Controller
@RequestMapping("/admin/addresses")
@RequiredArgsConstructor
public class AddressAdminController {

    private final IAddressAdminService addressService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("households", addressService.list());
        return "admin/addresses";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new SaveAddressRequest(null, "", "", ""));
        model.addAttribute("streets", addressService.streets());
        model.addAttribute("formAction", "/admin/addresses");
        model.addAttribute("formTitle", "Новый адрес");
        return "admin/address-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") SaveAddressRequest form,
                         BindingResult br, Model model) {
        validateStreet(form, br);
        if (br.hasErrors()) {
            model.addAttribute("streets", addressService.streets());
            model.addAttribute("formAction", "/admin/addresses");
            model.addAttribute("formTitle", "Новый адрес");
            return "admin/address-form";
        }
        addressService.create(form);
        return "redirect:/admin/addresses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var h = addressService.getForEdit(id);
        model.addAttribute("form", new SaveAddressRequest(h.streetId(), "", h.number(), h.description()));
        model.addAttribute("streets", addressService.streets());
        model.addAttribute("formAction", "/admin/addresses/" + id);
        model.addAttribute("formTitle", "Изменить адрес: " + h.addressLabel());
        return "admin/address-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") SaveAddressRequest form,
                         BindingResult br, Model model) {
        validateStreet(form, br);
        if (br.hasErrors()) {
            model.addAttribute("streets", addressService.streets());
            model.addAttribute("formAction", "/admin/addresses/" + id);
            model.addAttribute("formTitle", "Изменить адрес");
            return "admin/address-form";
        }
        addressService.update(id, form);
        return "redirect:/admin/addresses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        addressService.delete(id);
        return "redirect:/admin/addresses";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("household", addressService.detail(id));
        model.addAttribute("inhabitantForm", new SaveInhabitantRequest("", "", false));
        return "admin/address-detail";
    }

    @PostMapping("/{id}/inhabitants")
    public String addInhabitant(@PathVariable Long id,
                                @Valid @ModelAttribute("inhabitantForm") SaveInhabitantRequest form,
                                BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("household", addressService.detail(id));
            return "admin/address-detail";
        }
        addressService.addInhabitant(id, form);
        return "redirect:/admin/addresses/" + id;
    }

    @PostMapping("/{id}/inhabitants/{inhabitantId}")
    public String updateInhabitant(@PathVariable Long id, @PathVariable Long inhabitantId,
                                    @Valid @ModelAttribute("inhabitantForm") SaveInhabitantRequest form,
                                    BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("household", addressService.detail(id));
            return "admin/address-detail";
        }
        addressService.updateInhabitant(inhabitantId, form);
        return "redirect:/admin/addresses/" + id;
    }

    @PostMapping("/{id}/inhabitants/{inhabitantId}/delete")
    public String deleteInhabitant(@PathVariable Long id, @PathVariable Long inhabitantId) {
        addressService.deleteInhabitant(inhabitantId);
        return "redirect:/admin/addresses/" + id;
    }

    /** Улица обязательна: либо выбрана из списка, либо введена новая. */
    private void validateStreet(SaveAddressRequest form, BindingResult br) {
        if (form.streetId() == null && (form.newStreet() == null || form.newStreet().isBlank())) {
            br.rejectValue("newStreet", "street.required", "Выберите улицу или укажите новую");
        }
    }
}

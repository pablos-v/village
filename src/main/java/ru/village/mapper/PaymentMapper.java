package ru.village.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.village.controller.dto.response.PaymentResponse;
import ru.village.domain.Payment;

/** Payment entity → PaymentResponse DTO. Адрес склеивается из street + bldng. */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "date", source = "paydate")
    @Mapping(target = "address", source = ".", qualifiedByName = "addressLabel")
    @Mapping(target = "eventName", source = "event.name")
    PaymentResponse toResponse(Payment payment);

    @Named("addressLabel")
    default String addressLabel(Payment p) {
        var a = p.getHousehold().getAddress();
        return "ул. " + a.getStreet().getName() + ", д. " + a.getBldng().getNumber();
    }
}

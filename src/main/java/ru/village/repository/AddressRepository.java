package ru.village.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.village.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

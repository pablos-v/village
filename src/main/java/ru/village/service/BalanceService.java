package ru.village.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.village.repository.BalanceViewRepository;

/** Чтение текущего остатка из VIEW balance_view. */
@Service
@RequiredArgsConstructor
public class BalanceService implements IBalanceService {

    private final BalanceViewRepository balanceViewRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal currentBalance() {
        return balanceViewRepository.findCurrent().getAmount();
    }
}

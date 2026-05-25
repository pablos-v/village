package ru.village.service;

import java.util.List;
import ru.village.controller.dto.response.HistoryPeriod;

public interface IHistoryService {
    List<HistoryPeriod> periods();
}

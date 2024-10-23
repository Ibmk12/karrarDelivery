package com.karrardelivery.service;

import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.model.Trader;

import java.util.List;

public interface TraderService {

    Trader createTrader(TraderDto traderDto);
    List<Trader> getAllTraders();
    Trader updateTrader(Long id, TraderDto traderDto);
    void deleteTrader(Long id);
}

package com.karrardelivery.service;

import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;

import java.util.List;

public interface TraderService {

    GenericResponse<String> createTrader(TraderDto traderDto);
    List<Trader> getAllTraders();
    Trader updateTrader(Long id, TraderDto traderDto);
    void deleteTrader(Long id);
}

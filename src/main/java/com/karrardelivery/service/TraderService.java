package com.karrardelivery.service;

import com.karrardelivery.controller.spec.TraderSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;

import java.util.List;

public interface TraderService {

    GenericResponse<String> createTrader(TraderDto traderDto);
    GenericResponse<List<TraderDto>>  getAllTraders(TraderSpec spec);
    GenericResponse<TraderDto>  getTraderById(Long id);
    GenericResponse<String> updateTrader(Long id, TraderDto traderDto);
    void deleteTrader(Long id);
}

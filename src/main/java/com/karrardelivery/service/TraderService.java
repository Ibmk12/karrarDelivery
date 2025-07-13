package com.karrardelivery.service;

import com.karrardelivery.controller.spec.TraderSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TraderService {

    GenericResponse<String> createTrader(TraderDto traderDto);
    GenericResponse<List<TraderDto>> getAllTraders(TraderSpec spec, Pageable pageable);
    GenericResponse<TraderDto>  getTraderById(Long id);
    GenericResponse<String> updateTrader(Long id, TraderDto traderDto);
    GenericResponse<String> deleteTrader(Long id);
}

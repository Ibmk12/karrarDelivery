package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.controller.spec.TraderSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;
import com.karrardelivery.service.TraderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiUrls.TRADER)
@RequiredArgsConstructor
public class TraderController {

    private final TraderService traderService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> createTrader(@RequestBody @Valid TraderDto traderDto) {
        GenericResponse<String> response = traderService.createTrader(traderDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public GenericResponse<List<TraderDto>> getAllTraders(TraderSpec spec) {
        return traderService.getAllTraders(spec);
    }

    @GetMapping("/{id}")
    public GenericResponse<TraderDto> getTraderById(@PathVariable Long id) {
        return traderService.getTraderById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> updateTrader(@PathVariable Long id, @RequestBody TraderDto traderDto) {
        traderService.updateTrader(id, traderDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteTrader(@PathVariable Long id) {
        traderService.deleteTrader(id);
        return ResponseEntity.noContent().build();
    }
}

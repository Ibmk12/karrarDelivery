package com.karrardelivery.controller;

import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.model.Trader;
import com.karrardelivery.service.TraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traders")
public class TraderController {

    @Autowired
    private TraderService traderService;

    @PostMapping
    public ResponseEntity<Trader> createTrader(@RequestBody TraderDto traderDto) {
        return ResponseEntity.ok(traderService.createTrader(traderDto));
    }

    @GetMapping
    public ResponseEntity<List<Trader>> getAllTraders() {
        return ResponseEntity.ok(traderService.getAllTraders());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trader> updateTrader(@PathVariable Long id, @RequestBody TraderDto traderDto) {
        return ResponseEntity.ok(traderService.updateTrader(id, traderDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrader(@PathVariable Long id) {
        traderService.deleteTrader(id);
        return ResponseEntity.noContent().build();
    }
}

package com.karrardelivery.controller;

import com.karrardelivery.dto.EmirateDto;
import com.karrardelivery.model.Emirate;
import com.karrardelivery.service.EmirateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emirates")
public class EmirateController {

    @Autowired
    private EmirateService emirateService;

    @PostMapping
    public ResponseEntity<Emirate> createEmirate(@RequestBody EmirateDto emirateDto) {
        return ResponseEntity.ok(emirateService.createEmirate(emirateDto));
    }

    @GetMapping
    public ResponseEntity<List<Emirate>> getAllEmirates() {
        return ResponseEntity.ok(emirateService.getAllEmirates());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emirate> updateEmirate(@PathVariable Long id, @RequestBody EmirateDto emirateDto) {
        return ResponseEntity.ok(emirateService.updateEmirate(id, emirateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmirate(@PathVariable Long id) {
        emirateService.deleteEmirate(id);
        return ResponseEntity.noContent().build();
    }
}

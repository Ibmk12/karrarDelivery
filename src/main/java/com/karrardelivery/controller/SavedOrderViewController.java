package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.SavedOrderViewDto;
import com.karrardelivery.service.SavedOrderViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** The logged-in user's saved filter views for the Orders page. */
@RestController
@RequestMapping(ApiUrls.SAVED_ORDER_VIEWS)
@RequiredArgsConstructor
public class SavedOrderViewController {

    private final SavedOrderViewService service;

    @GetMapping
    public ResponseEntity<GenericResponse<List<SavedOrderViewDto>>> getMyViews() {
        return ResponseEntity.ok(service.getMyViews());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<SavedOrderViewDto>> createView(@RequestBody SavedOrderViewDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createView(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteView(@PathVariable Long id) {
        service.deleteView(id);
        return ResponseEntity.noContent().build();
    }
}

package com.karrardelivery.service;

import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.SavedOrderViewDto;

import java.util.List;

/** Saved Orders-page filter views of the logged-in user. */
public interface SavedOrderViewService {

    GenericResponse<List<SavedOrderViewDto>> getMyViews();

    GenericResponse<SavedOrderViewDto> createView(SavedOrderViewDto dto);

    void deleteView(Long id);
}

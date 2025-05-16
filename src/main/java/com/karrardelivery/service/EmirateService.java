package com.karrardelivery.service;

import com.karrardelivery.dto.EmirateDto;
import com.karrardelivery.entity.Emirate;

import java.util.List;

public interface EmirateService {

    Emirate createEmirate(EmirateDto emirateDto);

    List<Emirate> getAllEmirates();

    Emirate updateEmirate(Long id, EmirateDto emirateDto);

    void deleteEmirate(Long id);
}

package com.karrardelivery.service.impl;

import com.karrardelivery.dto.EmirateDto;
import com.karrardelivery.mapper.EmirateMapper;
import com.karrardelivery.model.Emirate;
import com.karrardelivery.repository.EmirateRepository;
import com.karrardelivery.service.EmirateService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmirateServiceImpl implements EmirateService {

    private final EmirateRepository emirateRepository;
    private final EmirateMapper emirateMapper;

    @Override
    public Emirate createEmirate(EmirateDto emirateDto) {
        Emirate emirate = emirateMapper.toEntity(emirateDto);
        emirate.setStatus("Active");
        return emirateRepository.save(emirate);
    }

    @Override
    public List<Emirate> getAllEmirates() {
        return emirateRepository.findAll();
    }

    @Override
    public Emirate updateEmirate(Long id, EmirateDto emirateDto) {
        Emirate emirate = emirateRepository.findById(id).orElseThrow();
        emirate.setName(emirateDto.getName());
        emirate.setRegionCode(emirateDto.getRegionCode());
        return emirateRepository.save(emirate);
    }

    @Override
    public void deleteEmirate(Long id) {
        emirateRepository.deleteById(id);
    }
}

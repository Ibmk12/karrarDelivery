package com.karrardelivery.service;

import com.karrardelivery.controller.spec.AgentSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.AgentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AgentService {

    GenericResponse<String> createAgent(AgentDto AgentDto);
    GenericResponse<List<AgentDto>> getAllAgents(AgentSpec spec, Pageable pageable, Integer page, Integer size);
    GenericResponse<AgentDto>  getAgentById(Long id);
    GenericResponse<String> updateAgent(Long id, AgentDto AgentDto);
    GenericResponse<String> deleteAgent(Long id);
}

package com.karrardelivery.service.impl;

import com.karrardelivery.constant.Messages;
import com.karrardelivery.controller.spec.AgentSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.AgentDto;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Agent;
import com.karrardelivery.exception.DuplicateResourceException;
import com.karrardelivery.exception.ResourceNotFoundException;
import com.karrardelivery.mapper.AgentMapper;
import com.karrardelivery.repository.AgentRepository;
import com.karrardelivery.service.AgentService;
import com.karrardelivery.service.MessageService;
import com.karrardelivery.service.AgentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.karrardelivery.constant.ErrorCodes.*;
import static com.karrardelivery.constant.Messages.*;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;
    private final MessageService messageService;

    @Override
    public GenericResponse<String> createAgent(AgentDto agentDto) {
        if(agentRepository.existsByPhoneNumberAndDeleted(agentDto.getPhoneNumber(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.phone.err.msg"), DUPLICATE_AGENT_PHONE_NUMBER_ERR_CODE);

        if(agentRepository.existsByNameAndDeleted(agentDto.getName(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.name.err.msg"), DUPLICATE_AGENT_NAME_ERR_CODE);

//        if(agentRepository.existsByCodeAndDeleted(agentDto.getCode(), false))
//            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.code.err.msg"), DUPLICATE_AGENT_CODE_ERR_CODE);

        if(agentRepository.existsByEmailAndDeleted(agentDto.getEmail(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.email.err.msg"), DUPLICATE_AGENT_EMAIL_ERR_CODE);

        Agent agent = agentMapper.toEntity(agentDto);
        agent.setDeleted(false);
        agent.setStatus(ACTIVE_STATUS);
        agentRepository.save(agent);
        return GenericResponse.successResponseWithoutData(TRADER_CREATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<List<AgentDto>> getAllAgents(AgentSpec spec, Pageable pageable, Integer page, Integer size) {

        if(page != null || size != null) {
            Page<AgentDto> result = agentMapper.mapToDtoPageable(agentRepository.findAll(spec, pageable));
            return GenericResponse.successResponseWithPagination(result.getContent(), result, DATA_FETCHED_SUCCESSFULLY);
        }

        List<AgentDto> result = agentMapper.toDtoList(agentRepository.findAll(spec));
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<AgentDto> getAgentById(Long id) {
        Agent agent = getActiveAgentByIdOrThrow(id);
        AgentDto result = agentMapper.toDto(agent);
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> updateAgent(Long id, AgentDto agentDto) {
        Agent agent = getActiveAgentByIdOrThrow(id);

        if(agentRepository.existsByPhoneNumberAndDeletedAndIdNot(agentDto.getPhoneNumber(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.phone.err.msg"), DUPLICATE_AGENT_PHONE_NUMBER_ERR_CODE);
        }
        if(agentRepository.existsByEmailAndDeletedAndIdNot(agentDto.getEmail(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.email.err.msg"), DUPLICATE_AGENT_EMAIL_ERR_CODE);
        }

//        if(agentRepository.existsByCodeAndDeletedAndIdNot(agentDto.getCode(), false, id)) {
//            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.code.err.msg"), DUPLICATE_AGENT_CODE_ERR_CODE);
//        }

        if(agentRepository.existsByNameAndDeletedAndIdNot(agentDto.getName(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.agent.name.err.msg"), DUPLICATE_AGENT_NAME_ERR_CODE);
        }

        agentMapper.mapToUpdate(agent, agentDto);
        agentRepository.save(agent);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    @Transactional
    public GenericResponse<String> deleteAgent(Long id) {
        Agent agent = getActiveAgentByIdOrThrow(id);
        agent.setDeleted(true);
        return GenericResponse.successResponseWithoutData(DATA_DELETED_SUCCESSFULLY);
    }

    public Agent getActiveAgentByIdOrThrow(Long AgentId) {
        return agentRepository.findByIdAndDeleted(AgentId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("agent.not.found.err.msg"),
                        AGENT_NOT_FOUND_ERR_CODE
                ));
    }
}

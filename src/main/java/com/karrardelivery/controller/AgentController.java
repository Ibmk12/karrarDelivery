package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.controller.spec.AgentSpec;
import com.karrardelivery.dto.AgentDto;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiUrls.AGENT)
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public ResponseEntity<GenericResponse<String>> createAgent(@RequestBody @Valid AgentDto agentDto) {
        GenericResponse<String> response = agentService.createAgent(agentDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public GenericResponse<List<AgentDto>> getAllAgents(AgentSpec spec, @PageableDefault(size = 10) Pageable pageable) {
        return agentService.getAllAgents(spec, pageable);
    }

    @GetMapping("/{id}")
    public GenericResponse<AgentDto> getAgentById(@PathVariable Long id) {
        return agentService.getAgentById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> updateAgent(@PathVariable Long id, @RequestBody AgentDto agentDto) {
        agentService.updateAgent(id, agentDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
}
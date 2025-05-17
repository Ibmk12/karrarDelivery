package com.karrardelivery.service.impl;

import com.karrardelivery.constant.ErrorCodes;
import com.karrardelivery.constant.Messages;
import com.karrardelivery.controller.spec.TraderSpec;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.entity.Trader;
import com.karrardelivery.exception.DuplicateResourceException;
import com.karrardelivery.exception.ResourceNotFoundException;
import com.karrardelivery.mapper.TraderMapper;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.MessageService;
import com.karrardelivery.service.TraderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.karrardelivery.constant.ErrorCodes.*;
import static com.karrardelivery.constant.Messages.*;

@Service
@RequiredArgsConstructor
public class TraderServiceImpl implements TraderService {

    private final TraderRepository traderRepository;
    private final TraderMapper traderMapper;
    private final MessageService messageService;

    @Override
    public GenericResponse<String> createTrader(TraderDto traderDto) {
        if(traderRepository.existsByPhoneNumberAndDeleted(traderDto.getPhoneNumber(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.phone.err.msg"), DUPLICATE_TRADER_PHONE_NUMBER_ERR_CODE);

        if(traderRepository.existsByEmailAndDeleted(traderDto.getEmail(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.email.err.msg"), DUPLICATE_TRADER_EMAIL_ERR_CODE);

        Trader trader = traderMapper.toEntity(traderDto);
        trader.setDeleted(false);
        trader.setStatus(ACTIVE_STATUS);
        traderRepository.save(trader);
        return GenericResponse.successResponseWithoutData(Messages.TRADER_CREATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<List<TraderDto>> getAllTraders(TraderSpec spec) {
        List<TraderDto> result = traderMapper.toDtoList(traderRepository.findAll(spec));
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<TraderDto> getTraderById(Long id) {
        Trader trader = traderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("trader.not.found.err.msg"),
                        TRADER_NOT_FOUND_ERR_CODE));

        TraderDto result = traderMapper.toDto(trader);
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> updateTrader(Long id, TraderDto traderDto) {
        Trader trader = traderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("trader.not.found.err.msg"),
                        TRADER_NOT_FOUND_ERR_CODE));

        if(traderRepository.existsByPhoneNumberAndDeletedAndIdNot(traderDto.getPhoneNumber(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.phone.err.msg"), DUPLICATE_TRADER_PHONE_NUMBER_ERR_CODE);
        }
        if(traderRepository.existsByEmailAndDeletedAndIdNot(traderDto.getEmail(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.email.err.msg"), DUPLICATE_TRADER_EMAIL_ERR_CODE);
        }

        traderMapper.mapToUpdate(trader, traderDto);
        traderRepository.save(trader);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    public void deleteTrader(Long id) {
        traderRepository.deleteById(id);
    }
}

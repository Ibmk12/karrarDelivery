package com.karrardelivery.service.impl;

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
public class TraderServiceImpl implements TraderService {

    private final TraderRepository traderRepository;
    private final TraderMapper traderMapper;
    private final MessageService messageService;

    @Override
    public GenericResponse<String> createTrader(TraderDto traderDto) {
        if(traderRepository.existsByPhoneNumberAndDeleted(traderDto.getPhoneNumber(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.phone.err.msg"), DUPLICATE_TRADER_PHONE_NUMBER_ERR_CODE);

        if(traderRepository.existsByNameAndDeleted(traderDto.getName(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.name.err.msg"), DUPLICATE_TRADER_NAME_ERR_CODE);

        if(traderRepository.existsByCodeAndDeleted(traderDto.getCode(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.code.err.msg"), DUPLICATE_TRADER_CODE_ERR_CODE);

        if(traderRepository.existsByEmailAndDeleted(traderDto.getEmail(), false))
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.email.err.msg"), DUPLICATE_TRADER_EMAIL_ERR_CODE);

        Trader trader = traderMapper.toEntity(traderDto);
        trader.setDeleted(false);
        trader.setStatus(ACTIVE_STATUS);
        traderRepository.save(trader);
        return GenericResponse.successResponseWithoutData(Messages.TRADER_CREATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<List<TraderDto>> getAllTraders(TraderSpec spec, Pageable pageable) {
        Page<TraderDto> result = traderMapper.mapToDtoPageable(traderRepository.findAll(spec, pageable));
        return GenericResponse.successResponseWithPagination(result.getContent(), result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<TraderDto> getTraderById(Long id) {
        Trader trader = getActiveTraderByIdOrThrow(id);
        TraderDto result = traderMapper.toDto(trader);
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> updateTrader(Long id, TraderDto traderDto) {
        Trader trader = getActiveTraderByIdOrThrow(id);

        if(traderRepository.existsByPhoneNumberAndDeletedAndIdNot(traderDto.getPhoneNumber(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.phone.err.msg"), DUPLICATE_TRADER_PHONE_NUMBER_ERR_CODE);
        }
        if(traderRepository.existsByEmailAndDeletedAndIdNot(traderDto.getEmail(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.email.err.msg"), DUPLICATE_TRADER_EMAIL_ERR_CODE);
        }

        if(traderRepository.existsByCodeAndDeletedAndIdNot(traderDto.getEmail(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.code.err.msg"), DUPLICATE_TRADER_CODE_ERR_CODE);
        }

        if(traderRepository.existsByNameAndDeletedAndIdNot(traderDto.getEmail(), false, id)) {
            throw new DuplicateResourceException(messageService.getMessage("duplicate.trader.name.err.msg"), DUPLICATE_TRADER_NAME_ERR_CODE);
        }

        traderMapper.mapToUpdate(trader, traderDto);
        traderRepository.save(trader);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    @Transactional
    public GenericResponse<String> deleteTrader(Long id) {
        Trader trader = getActiveTraderByIdOrThrow(id);
        trader.setDeleted(true);
        return GenericResponse.successResponseWithoutData(DATA_DELETED_SUCCESSFULLY);
    }

    public Trader getActiveTraderByIdOrThrow(Long traderId) {
        return traderRepository.findByIdAndDeleted(traderId, false)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("trader.not.found.err.msg"),
                        TRADER_NOT_FOUND_ERR_CODE
                ));
    }
}

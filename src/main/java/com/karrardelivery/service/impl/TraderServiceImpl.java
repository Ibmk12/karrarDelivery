package com.karrardelivery.service.impl;

import com.karrardelivery.dto.TraderDto;
import com.karrardelivery.model.Trader;
import com.karrardelivery.repository.TraderRepository;
import com.karrardelivery.service.TraderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraderServiceImpl implements TraderService {

    @Autowired
    private TraderRepository traderRepository;

    @Override
    public Trader createTrader(TraderDto traderDto) {
        Trader trader = new Trader();
        trader.setName(traderDto.getName());
        trader.setContactInfo(traderDto.getContactInfo());
        trader.setStatus("Active");
        return traderRepository.save(trader);
    }

    @Override
    public List<Trader> getAllTraders() {
        return traderRepository.findAll();
    }

    @Override
    public Trader updateTrader(Long id, TraderDto traderDto) {
        Trader trader = traderRepository.findById(id).orElseThrow();
        trader.setName(traderDto.getName());
        trader.setContactInfo(traderDto.getContactInfo());
        return traderRepository.save(trader);
    }

    @Override
    public void deleteTrader(Long id) {
        traderRepository.deleteById(id);
    }
}

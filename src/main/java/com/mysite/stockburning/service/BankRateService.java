package com.mysite.stockburning.service;

import com.mysite.stockburning.entity.BankRates;
import com.mysite.stockburning.repository.BankRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankRateService {
    private final BankRateRepository bankRateRepository;
    public List<BankRates> getAllBankRates(){
        return bankRateRepository.findAll();
    }
}

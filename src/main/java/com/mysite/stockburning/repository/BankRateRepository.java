package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.BankRates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRateRepository extends JpaRepository<BankRates, Long> {
}

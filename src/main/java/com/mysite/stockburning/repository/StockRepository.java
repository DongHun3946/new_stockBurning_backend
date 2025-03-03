package com.mysite.stockburning.repository;

import com.mysite.stockburning.entity.StockTickers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<StockTickers, Long> {
    List<StockTickers> findByStockSymbolContaining(String Ticker); //포함하고 있는 것
    StockTickers findByStockSymbol(String stockSymbol);            //완전일치

}

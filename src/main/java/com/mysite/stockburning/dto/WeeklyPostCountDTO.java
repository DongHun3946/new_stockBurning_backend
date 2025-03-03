package com.mysite.stockburning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WeeklyPostCountDTO {
    private String date;
    private int count;
    private int bullishCnt;
    private int bearishCnt;

    public static List<WeeklyPostCountDTO> of(Map<String, Map<String, Integer>> weeklyOpinionData, RealtimeStatDTO realtimeStatDTO){
        List<WeeklyPostCountDTO> dtoList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : weeklyOpinionData.entrySet()) {
            String date = entry.getKey();
            Map<String, Integer> stats = entry.getValue();

            int count = stats.getOrDefault("count", 0);
            int bullishCnt = stats.getOrDefault("bullishCnt", 0);
            int bearishCnt = stats.getOrDefault("bearishCnt", 0);

            dtoList.add(new WeeklyPostCountDTO(date, count, bullishCnt, bearishCnt));
        }
        int count = realtimeStatDTO.getCount();
        LocalDate localDate = realtimeStatDTO.getDate();
        String date = localDate.toString();
        int bullishCnt = realtimeStatDTO.getBullishCnt();
        int bearishCnt = realtimeStatDTO.getBearishCnt();
        dtoList.add(new WeeklyPostCountDTO(date, count, bullishCnt, bearishCnt));
        return dtoList;
    }
}

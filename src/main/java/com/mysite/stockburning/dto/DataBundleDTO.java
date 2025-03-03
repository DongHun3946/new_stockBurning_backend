package com.mysite.stockburning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataBundleDTO {
    private StockDataDTO stockDataDTO;
    private List<PostDTO> postDTO;
    private RealtimeStatDTO realtimeStatDTO;
    private List<WeeklyPostCountDTO> weeklyPostCountDTO;
}

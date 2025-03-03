package com.mysite.stockburning.dto.request;

import com.mysite.stockburning.entity.StockTickers;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.util.Opinion;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostCreateRequest {
    private String content;
    private Opinion opinion;
    private String stockSymbol;
}

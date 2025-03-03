package com.mysite.stockburning.dto.request;

import com.mysite.stockburning.util.Opinion;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostUpdateRequest {
    private String content;
    private Opinion opinion;
    private int originalImage;
}

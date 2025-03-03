package com.mysite.stockburning.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;

@Getter
@NoArgsConstructor
public class KakaoResponse {

    @Getter
    private Long id;

    @Getter
    private String connectedAt;

    private Map<String, Object> properties;

    public KakaoResponse(Map<String, Object> attribute) {
        this.id = (Long)attribute.get("id");
        this.connectedAt = (String) attribute.get("connected_at");
        this.properties = (Map<String, Object>) attribute.get("properties");

    }

    public String getProvider(){
        return "KAKAO";
    }
    public String getNickname() {
        if (properties != null) {
            return (String) properties.get("nickname");
        }
        return null;
    }

    public String getProfileImageUrl() {
        if (properties != null) {
           return (String) properties.get("profile_image");
        }
        return null;
    }
}
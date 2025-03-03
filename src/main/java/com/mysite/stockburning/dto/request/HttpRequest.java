package com.mysite.stockburning.dto.request;

import org.springframework.http.HttpMethod;

public record HttpRequest(HttpMethod method, String urlPattern) {
}

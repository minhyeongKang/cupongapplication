package com.hellomeen.cupongapplication.service;

import com.hellomeen.cupongapplication.dto.naver.NaverShoppingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class NaverShoppingService {

    private final RestClient restClient;

    public NaverShoppingService(
            @Value("${naver.api.client-id}") String clientId,
            @Value("${naver.api.client-secret}") String clientSecret) {

        this.restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public List<NaverShoppingResponse.NaverShoppingItem> search(String query, int display) {
        try {
            NaverShoppingResponse response = restClient.get()
                    .uri("/v1/search/shop.json?query={query}&display={display}&sort=sim", query, display)
                    .retrieve()
                    .body(NaverShoppingResponse.class);

            if (response == null || response.getItems() == null) {
                return Collections.emptyList();
            }
            return response.getItems();
        } catch (Exception e) {
            log.warn("네이버 쇼핑 API 호출 실패 [query={}]: {}", query, e.getMessage());
            return Collections.emptyList();
        }
    }
}

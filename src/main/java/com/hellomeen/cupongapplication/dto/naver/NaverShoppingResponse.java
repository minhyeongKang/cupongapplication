package com.hellomeen.cupongapplication.dto.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverShoppingResponse {

    private int total;
    private List<NaverShoppingItem> items;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverShoppingItem {
        private String title;
        private String link;
        private String image;
        private String lprice;
        private String mallName;
        private String brand;
        private String category1;
        private String category2;

        public String cleanTitle() {
            return title == null ? "" : title.replaceAll("<[^>]*>", "").trim();
        }

        public int parsedPrice() {
            try {
                return Integer.parseInt(lprice);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}

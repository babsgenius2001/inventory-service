package com.ikea.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ProductArticle {

    @JsonProperty("art_id")
    private String id;

    @JsonProperty("amount_of")
    private String amount;
}

package com.ikea.inventory.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Products implements Serializable {
    private final List<ProductRequest> productRequests;

    public Products() {
        productRequests = new ArrayList<>();
    }

    public List<ProductRequest> getProducts() {
        return productRequests;
    }
}

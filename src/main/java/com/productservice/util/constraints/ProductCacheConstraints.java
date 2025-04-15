package com.productservice.util.constraints;

public enum ProductCacheConstraints {

    PRODUCT_KEY("ms-product:products:%s"),
    PRODUCT_CATEGORY_KEY("ms-product:category:%s"),
    PRODUCT_LIST_KEY("product:list:page_%s_size_%s");

    private final String keyFormat;

    ProductCacheConstraints(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public String getKey(Object... args) {
        return String.format(this.keyFormat, args);
    }
}

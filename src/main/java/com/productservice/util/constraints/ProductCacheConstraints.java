package com.productservice.util.constraints;

public enum ProductCacheConstraints {

    PRODUCT_KEY("ms-product:products:%s"),
    CATEGORY_KEY("ms-product:category:%s"),
    CATEGORY_TREE_KEY("ms-product:category:tree"),
    PRODUCT_LIST_KEY("product:list:page_%s_size_%s");

    private final String keyFormat;

    ProductCacheConstraints(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public String getKey(Object... args) {
        return String.format(this.keyFormat, args);
    }
}

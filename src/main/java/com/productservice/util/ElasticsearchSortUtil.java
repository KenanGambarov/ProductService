package com.productservice.util;

import com.productservice.search.ProductDocument;

import java.util.List;

public class ElasticsearchSortUtil {

    private static final List<String> keywordFields = List.of(
            ProductDocument.Fields.name,
            ProductDocument.Fields.description,
            ProductDocument.Fields.categoryName
    );

    public static final String KEYWORD_SUFFIX = "keyword";

    public static String resolveSortField(String sortBy) {
        if (keywordFields.contains(sortBy)) {
            return sortBy + "." + KEYWORD_SUFFIX;
        }
        return sortBy;
    }
}

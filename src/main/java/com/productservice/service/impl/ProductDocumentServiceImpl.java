package com.productservice.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.productservice.dto.response.ProductDocumentResponseDto;
import com.productservice.entity.ProductEntity;
import com.productservice.mapper.ProductDocumentMapper;
import com.productservice.search.ProductDocument;
import com.productservice.service.ProductDocumentService;
import com.productservice.util.ElasticsearchSortUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDocumentServiceImpl implements ProductDocumentService {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<ProductDocumentResponseDto> search(String keyword, int page, int size, String sortBy, String sortDirection) {
        try {
            int from = (page - 1) * size;

            SearchResponse<ProductDocument> response = elasticsearchClient.search(d -> d
                    .index(ProductDocument.INDEX_NAME)
                    .from(from)
                    .size(size)
                    .sort(sort -> sort.field(f -> f.field(ElasticsearchSortUtil.resolveSortField(sortBy)).
                            order(sortDirection.equalsIgnoreCase(SortOrder.Desc.jsonValue())?SortOrder.Desc:SortOrder.Asc)))
                    .query(q -> q
                            .bool(b -> b
                                    .should(s -> s.matchPhrasePrefix(m -> m.field(ProductDocument.Fields.name).query(keyword)))
                                    .should(s -> s.matchPhrasePrefix(m -> m.field(ProductDocument.Fields.description).query(keyword)))
                                    .should(s -> s.matchPhrasePrefix(m -> m.field(ProductDocument.Fields.categoryName).query(keyword)))
                                    .minimumShouldMatch("1")
                            )

                    ), ProductDocument.class);
            return ProductDocumentMapper.mapToDto(response.hits().hits().stream()
                    .map(Hit::source)
                    .toList());

        } catch (IOException e) {
            throw new RuntimeException("Product search failed", e);
        }
    }

    @Override
    public void index(ProductEntity entity) {
        ProductDocument doc = ProductDocumentMapper.mapToDto(entity);

        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(ProductDocument.INDEX_NAME)
                    .id(String.valueOf(doc.getId()))
                    .document(doc)
            );

            log.info("Indexed product with ID: {}", response.id());
        } catch (IOException e) {
            throw new RuntimeException("Failed to index product", e);
        }
    }

    @Override
    public void update(ProductEntity entity) {
        ProductDocument doc = ProductDocumentMapper.mapToDto(entity);

        try {
            UpdateResponse<ProductDocument> response = elasticsearchClient.update(u -> u
                            .index(ProductDocument.INDEX_NAME)
                            .id(String.valueOf(doc.getId()))
                            .doc(doc),
                    ProductDocument.class
            );

            log.info("Updated product with ID: {}", response.id());
        } catch (IOException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            DeleteResponse response = elasticsearchClient.delete(d -> d
                    .index(ProductDocument.INDEX_NAME)
                    .id(String.valueOf(id))
            );

            log.info("Deleted product with ID: {}", response.id());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete product from index", e);
        }
    }


    @Override
    public void reindex(String productName) {
        List<ProductDocument> originalDocs = fetchAllProductsFromES();
        List<ProductDocument> fixedDocs = fixCategoryNames(originalDocs,productName);
        reindexProducts(fixedDocs);
    }

    public List<ProductDocument> fetchAllProductsFromES() {
        try {
            SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                            .index(ProductDocument.INDEX_NAME)
                            .query(q -> q.matchAll(m -> m))
                            .size(10000), // ehtiyac varsa pagination et
                    ProductDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch documents for reindexing", e);
        }
    }

    public List<ProductDocument> fixCategoryNames(List<ProductDocument> products, String productName) {
        return products.stream()
                .peek(p -> {
                    if (productName.equalsIgnoreCase(p.getCategoryName())) {
                        p.setCategoryName(productName);
                    }
                })
                .toList();
    }

    public void reindexProducts(List<ProductDocument> correctedDocs) {
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();

            for (ProductDocument doc : correctedDocs) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(ProductDocument.INDEX_NAME)
                                .id(String.valueOf(doc.getId()))
                                .document(doc)
                        )
                );
            }

            BulkResponse result = elasticsearchClient.bulk(br.build());
            if (result.errors()) {
                log.error("Bulk indexing had errors");
                // log errors if needed
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to reindex documents", e);
        }
    }


}

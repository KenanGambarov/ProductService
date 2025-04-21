package com.productservice.search;

import com.productservice.util.ElasticsearchSortUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Document(indexName = ProductDocument.INDEX_NAME)
public class ProductDocument {

    @Id
    private Long id;

    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = @InnerField(suffix = ElasticsearchSortUtil.KEYWORD_SUFFIX, type = FieldType.Keyword)
    )
    private String name;

    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = @InnerField(suffix = ElasticsearchSortUtil.KEYWORD_SUFFIX, type = FieldType.Keyword)
    )
    private String description;

    @Field(type = FieldType.Double)
    private double price;

    @Field(type = FieldType.Date)
    private Date createdAt;

    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = @InnerField(suffix = ElasticsearchSortUtil.KEYWORD_SUFFIX, type = FieldType.Keyword)
    )
    private String categoryName;

    public static final String INDEX_NAME = "products";

}

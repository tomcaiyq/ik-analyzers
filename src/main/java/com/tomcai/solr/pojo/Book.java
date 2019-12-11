package com.tomcai.solr.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "book_core")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @Field
    private Long id;

    @Field
    private String name;

    @Field
    private Double price;

    @Field
    private String describe;
}

package com.tomcai.solr.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SolrDocument(collection = "island_core")
public class Island {
    @Id
    @Field
    @ExcelProperty("id")
    private Long id;

    @Field
    @ExcelProperty("name")
    private String name;

    @Field
    @ExcelProperty("description")
    private String description;
}

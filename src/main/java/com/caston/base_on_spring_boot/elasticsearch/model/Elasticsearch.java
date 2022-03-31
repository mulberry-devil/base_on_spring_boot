package com.caston.base_on_spring_boot.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "elasticsearch")
public class Elasticsearch implements Serializable {
    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private Long num;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String desc;

    @Field(type = FieldType.Keyword, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String name;

    private Map<String, List<String>> highlights;
}
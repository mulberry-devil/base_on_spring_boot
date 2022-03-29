package com.caston.base_on_spring_boot.elasticsearch.repository;

import com.caston.base_on_spring_boot.elasticsearch.model.Elasticsearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESRepository extends ElasticsearchRepository<Elasticsearch, Integer> {
}

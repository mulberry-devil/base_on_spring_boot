package com.caston.base_on_spring_boot.elasticsearch.service;

import com.caston.base_on_spring_boot.elasticsearch.dto.PageResponse;
import com.caston.base_on_spring_boot.elasticsearch.model.Elasticsearch;

import java.util.List;

public interface ElasticsearchService {
    void saveAll(List<Elasticsearch> elasticsearchs);

    Elasticsearch findById(Integer id);

    void deleteById(Integer id);

    void updateById(Elasticsearch elasticsearch);

    PageResponse<Elasticsearch> findList(Elasticsearch elasticsearch, Integer pageIndex, Integer pageSize);

    PageResponse<Elasticsearch> findAll(Integer pageIndex, Integer pageSize);

    PageResponse<Elasticsearch> findHighlight(Elasticsearch elasticsearch, Integer pageIndex, Integer pageSize);
}

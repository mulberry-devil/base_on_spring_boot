package com.caston.base_on_spring_boot.elasticsearch.service.impl;

import com.caston.base_on_spring_boot.elasticsearch.dto.PageResponse;
import com.caston.base_on_spring_boot.elasticsearch.model.Elasticsearch;
import com.caston.base_on_spring_boot.elasticsearch.repository.ESRepository;
import com.caston.base_on_spring_boot.elasticsearch.service.ElasticsearchService;
import groovy.util.logging.Slf4j;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private ESRepository esRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void saveAll(List<Elasticsearch> elasticsearches) {
        esRepository.saveAll(elasticsearches);
    }

    @Override
    public void deleteById(Integer id) {
        esRepository.deleteById(id);
    }

    @Override
    public void updateById(Elasticsearch elasticsearch) {
        esRepository.save(elasticsearch);
    }

    @Override
    public PageResponse<Elasticsearch> findList(Elasticsearch elasticsearch, Integer pageIndex, Integer pageSize) {
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria()
                .and(new Criteria("desc").contains(elasticsearch.getDesc()))
                .and(new Criteria("num").is(elasticsearch.getNum())))
                .setPageable(PageRequest.of(pageIndex, pageSize));

        SearchHits<Elasticsearch> searchHits = elasticsearchRestTemplate.search(criteriaQuery, Elasticsearch.class);
        List<Elasticsearch> result = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        PageResponse<Elasticsearch> pageResponse = new PageResponse<>();
        pageResponse.setTotal(searchHits.getTotalHits());
        pageResponse.setResult(result);
        return pageResponse;
    }

    @Override
    public PageResponse<Elasticsearch> findAll(Integer pageIndex, Integer pageSize) {
        Page<Elasticsearch> page = esRepository.findAll(PageRequest.of(pageIndex, pageSize));
        PageResponse<Elasticsearch> pageResponse = new PageResponse<>();
        pageResponse.setTotal(page.getTotalElements());
        pageResponse.setResult(page.getContent());
        return pageResponse;
    }

    @Override
    public PageResponse<Elasticsearch> findHighlight(Elasticsearch elasticsearch, Integer pageIndex, Integer pageSize) {
        if (elasticsearch == null) {
            PageResponse<Elasticsearch> pageResponse = new PageResponse<>();
            pageResponse.setTotal(0L);
            pageResponse.setResult(new ArrayList<>());
            return pageResponse;
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria()
                .and(new Criteria("num").is(elasticsearch.getNum()))
                .and(new Criteria("desc").contains(elasticsearch.getDesc())))
                .setPageable(PageRequest.of(pageIndex, pageSize));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("num").field("desc").field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<h3 style='color:blue'>");
        highlightBuilder.postTags("</h3>");

        HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
        criteriaQuery.setHighlightQuery(highlightQuery);

        SearchHits<Elasticsearch> searchHits = elasticsearchRestTemplate.search(criteriaQuery, Elasticsearch.class);

        List<Elasticsearch> result = searchHits.get().map(e -> {
            Elasticsearch element = e.getContent();
            element.setHighlights(e.getHighlightFields());
            return element;
        }).collect(Collectors.toList());

        PageResponse<Elasticsearch> pageResponse = new PageResponse<>();
        pageResponse.setTotal(searchHits.getTotalHits());
        pageResponse.setResult(result);
        return pageResponse;
    }

    @Override
    public Elasticsearch findById(Integer id) {
        return esRepository.findById(id).orElse(null);
    }
}

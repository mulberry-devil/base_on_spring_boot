package com.caston.base_on_spring_boot.elasticsearch.service.impl;

import com.caston.base_on_spring_boot.elasticsearch.dto.PageResponse;
import com.caston.base_on_spring_boot.elasticsearch.model.Order;
import com.caston.base_on_spring_boot.elasticsearch.repository.OrderRepository;
import com.caston.base_on_spring_boot.elasticsearch.service.OrderService;
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
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void saveAll(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

    @Override
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void updateById(Order order) {
        orderRepository.save(order);
    }

    @Override
    public PageResponse<Order> findList(Order order, Integer pageIndex, Integer pageSize) {
        CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria()
                .and(new Criteria("orderDesc").contains(order.getOrderDesc()))
                .and(new Criteria("orderNo").is(order.getOrderNo())))
                .setPageable(PageRequest.of(pageIndex, pageSize));

        SearchHits<Order> searchHits = elasticsearchRestTemplate.search(criteriaQuery, Order.class);
        List<Order> result = searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        PageResponse<Order> pageResponse = new PageResponse<Order>();
        pageResponse.setTotal(searchHits.getTotalHits());
        pageResponse.setResult(result);
        return pageResponse;
    }

    @Override
    public PageResponse<Order> findAll(Integer pageIndex, Integer pageSize) {
        Page<Order> page = orderRepository.findAll(PageRequest.of(pageIndex, pageSize));

        PageResponse<Order> pageResponse = new PageResponse<Order>();
        pageResponse.setTotal(page.getTotalElements());
        pageResponse.setResult(page.getContent());
        return pageResponse;
    }

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }
}

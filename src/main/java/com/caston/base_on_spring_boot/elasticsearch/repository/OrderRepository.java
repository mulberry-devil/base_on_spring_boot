package com.caston.base_on_spring_boot.elasticsearch.repository;

import com.caston.base_on_spring_boot.elasticsearch.model.Order;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrderRepository extends ElasticsearchRepository<Order, Integer> {
}

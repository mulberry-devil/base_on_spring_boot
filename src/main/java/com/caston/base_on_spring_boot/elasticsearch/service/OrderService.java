package com.caston.base_on_spring_boot.elasticsearch.service;

import com.caston.base_on_spring_boot.elasticsearch.dto.PageResponse;
import com.caston.base_on_spring_boot.elasticsearch.model.Order;

import java.util.List;

public interface OrderService {
    void saveAll(List<Order> orders);

    Order findById(Integer id);

    void deleteById(Integer id);

    void updateById(Order order);

    PageResponse<Order> findList(Order order, Integer pageIndex, Integer pageSize);

    PageResponse<Order> findAll(Integer pageIndex, Integer pageSize);

}

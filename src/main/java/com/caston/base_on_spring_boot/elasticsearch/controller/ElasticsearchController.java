package com.caston.base_on_spring_boot.elasticsearch.controller;

import com.caston.base_on_spring_boot.elasticsearch.model.Order;
import com.caston.base_on_spring_boot.elasticsearch.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/elasticsearch")
@RestController
public class ElasticsearchController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    OrderService orderService;

    /**
     * 创建索引
     */
    @GetMapping("create")
    public String create(@RequestParam String indexName) {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(indexName));
        if (indexOperations.exists()) {
            return "索引已存在";
        }
        indexOperations.create();
        return "索引创建成功";
    }

    /**
     * 删除索引
     */
    @GetMapping("delete")
    public String delete(@RequestParam String indexName) {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(indexName));
        indexOperations.delete();
        return "索引删除成功";
    }

    /**
     * 批量创建
     */
    @PostMapping("saveBatch")
    public String saveBatch(@RequestBody List<Order> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return "文档不能为空";
        }
        orderService.saveAll(orders);
        return "保存成功";
    }

    /**
     * 根据id删除
     */
    @GetMapping("deleteById")
    public String deleteById(@RequestParam Integer id) {
        orderService.deleteById(id);
        return "删除成功";
    }

    /**
     * 根据id更新
     */
    @PostMapping("updateById")
    public String updateById(@RequestBody Order order) {
        orderService.updateById(order);
        return "更新成功";
    }

    /**
     * 根据id搜索
     */
    @GetMapping("findById")
    public Order findById(@RequestParam Integer id) {
        return orderService.findById(id);
    }

    /**
     * 分页搜索所有
     */
//    @GetMapping("findAll")
//    public String findAll(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
//        return JSON.toJSONString(orderService.findAll(pageIndex, pageSize));
//    }
//
//    /**
//     * 条件分页搜索
//     */
//    @GetMapping("findList")
//    public String findList(@RequestBody Order order, @RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
//        return JSON.toJSONString(orderService.findList(order, pageIndex, pageSize));
//    }
}

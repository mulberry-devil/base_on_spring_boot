package com.caston.base_on_spring_boot.elasticsearch.controller;

import com.caston.base_on_spring_boot.elasticsearch.dto.PageResponse;
import com.caston.base_on_spring_boot.elasticsearch.model.Elasticsearch;
import com.caston.base_on_spring_boot.elasticsearch.service.ElasticsearchService;
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
    ElasticsearchService elasticsearchService;

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
    public String saveBatch(@RequestBody List<Elasticsearch> elasticsearches) {
        if (CollectionUtils.isEmpty(elasticsearches)) {
            return "文档不能为空";
        }
        elasticsearchService.saveAll(elasticsearches);
        return "保存成功";
    }

    /**
     * 根据id删除
     */
    @GetMapping("deleteById")
    public String deleteById(@RequestParam Integer id) {
        elasticsearchService.deleteById(id);
        return "删除成功";
    }

    /**
     * 根据id更新
     */
    @PostMapping("updateById")
    public String updateById(@RequestBody Elasticsearch order) {
        elasticsearchService.updateById(order);
        return "更新成功";
    }

    /**
     * 根据id搜索
     */
    @GetMapping("findById")
    public Elasticsearch findById(@RequestParam Integer id) {
        return elasticsearchService.findById(id);
    }

    /**
     * 分页搜索所有
     */
    @GetMapping("findAll")
    public PageResponse<Elasticsearch> findAll(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return elasticsearchService.findAll(pageIndex, pageSize);
    }

    /**
     * 条件分页搜索
     */
    @GetMapping("findList")
    public PageResponse<Elasticsearch> findList(Elasticsearch elasticsearch, @RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return elasticsearchService.findList(elasticsearch, pageIndex, pageSize);
    }

    /**
     * 条件高亮分页搜索
     */
    @GetMapping("findHighlight")
    public PageResponse<Elasticsearch> findHighlight(Elasticsearch elasticsearch, @RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return elasticsearchService.findHighlight(elasticsearch, pageIndex, pageSize);
    }
}

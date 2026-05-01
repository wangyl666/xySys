package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.Material;
import com.supplychain.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "物料管理")
@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @ApiOperation("分页查询物料")
    @GetMapping("/page")
    public Result<Page<Material>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            Material query) {
        Page<Material> page = new Page<>(current, size);
        Page<Material> result = materialService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("查询所有物料列表")
    @GetMapping("/list")
    public Result<List<Material>> listAll() {
        List<Material> list = materialService.listAll();
        return Result.success(list);
    }

    @ApiOperation("获取物料详情")
    @GetMapping("/{id}")
    public Result<Material> getById(
            @ApiParam("物料ID") @PathVariable Long id) {
        Material material = materialService.getDetail(id);
        return Result.success(material);
    }

    @ApiOperation("创建物料")
    @PostMapping
    public Result<Material> create(@RequestBody Material material) {
        Material result = materialService.create(material);
        return Result.success(result);
    }

    @ApiOperation("更新物料")
    @PutMapping("/{id}")
    public Result<Material> update(
            @ApiParam("物料ID") @PathVariable Long id,
            @RequestBody Material material) {
        material.setId(id);
        Material result = materialService.update(material);
        return Result.success(result);
    }

    @ApiOperation("删除物料")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("物料ID") @PathVariable Long id) {
        materialService.delete(id);
        return Result.success();
    }
}

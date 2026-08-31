package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.Supplier;
import com.supplychain.service.SupplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "供应商管理")
@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @ApiOperation("分页查询供应商")
    @GetMapping("/page")
    public Result<Page<Supplier>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            Supplier query) {
        Page<Supplier> page = new Page<>(current, size);
        Page<Supplier> result = supplierService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("查询所有供应商列表")
    @GetMapping("/list")
    public Result<List<Supplier>> listAll() {
        List<Supplier> list = supplierService.listAll();
        return Result.success(list);
    }

    @ApiOperation("获取供应商详情")
    @GetMapping("/{id}")
    public Result<Supplier> getById(
            @ApiParam("供应商ID") @PathVariable Long id) {
        Supplier supplier = supplierService.getDetail(id);
        return Result.success(supplier);
    }

    @ApiOperation("创建供应商")
    @PostMapping
    public Result<Supplier> create(@RequestBody Supplier supplier) {
        Supplier result = supplierService.create(supplier);
        return Result.success(result);
    }

    @ApiOperation("更新供应商")
    @PutMapping("/{id}")
    public Result<Supplier> update(
            @ApiParam("供应商ID") @PathVariable Long id,
            @RequestBody Supplier supplier) {
        supplier.setId(id);
        Supplier result = supplierService.update(supplier);
        return Result.success(result);
    }

    @ApiOperation("删除供应商")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("供应商ID") @PathVariable Long id) {
        supplierService.delete(id);
        return Result.success();
    }
}

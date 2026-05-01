package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.BillType;
import com.supplychain.service.BillTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "单据类型管理")
@RestController
@RequestMapping("/api/bill-type")
@RequiredArgsConstructor
public class BillTypeController {

    private final BillTypeService billTypeService;

    @ApiOperation("分页查询单据类型")
    @GetMapping("/page")
    public Result<Page<BillType>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            BillType query) {
        Page<BillType> page = new Page<>(current, size);
        Page<BillType> result = billTypeService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("查询所有启用的单据类型列表")
    @GetMapping("/list")
    public Result<List<BillType>> listAll() {
        List<BillType> list = billTypeService.listAll();
        return Result.success(list);
    }

    @ApiOperation("获取单据类型详情")
    @GetMapping("/{id}")
    public Result<BillType> getById(
            @ApiParam("单据类型ID") @PathVariable Long id) {
        BillType billType = billTypeService.getDetail(id);
        return Result.success(billType);
    }

    @ApiOperation("创建单据类型")
    @PostMapping
    public Result<BillType> create(@RequestBody BillType billType) {
        BillType result = billTypeService.create(billType);
        return Result.success(result);
    }

    @ApiOperation("更新单据类型")
    @PutMapping("/{id}")
    public Result<BillType> update(
            @ApiParam("单据类型ID") @PathVariable Long id,
            @RequestBody BillType billType) {
        billType.setId(id);
        BillType result = billTypeService.update(billType);
        return Result.success(result);
    }

    @ApiOperation("删除单据类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("单据类型ID") @PathVariable Long id) {
        billTypeService.delete(id);
        return Result.success();
    }

    @ApiOperation("启用单据类型")
    @PostMapping("/{id}/enable")
    public Result<Void> enable(
            @ApiParam("单据类型ID") @PathVariable Long id) {
        billTypeService.enable(id);
        return Result.success();
    }

    @ApiOperation("禁用单据类型")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(
            @ApiParam("单据类型ID") @PathVariable Long id) {
        billTypeService.disable(id);
        return Result.success();
    }
}

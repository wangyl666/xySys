package com.supplychain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supplychain.common.Result;
import com.supplychain.entity.BillFlowConfig;
import com.supplychain.service.BillFlowConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "单据审批流配置管理")
@RestController
@RequestMapping("/api/bill-flow-config")
@RequiredArgsConstructor
public class BillFlowConfigController {

    private final BillFlowConfigService billFlowConfigService;

    @ApiOperation("分页查询单据审批流配置")
    @GetMapping("/page")
    public Result<Page<BillFlowConfig>> pageQuery(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Long size,
            BillFlowConfig query) {
        Page<BillFlowConfig> page = new Page<>(current, size);
        Page<BillFlowConfig> result = billFlowConfigService.pageQuery(page, query);
        return Result.success(result);
    }

    @ApiOperation("根据单据类型编码查询配置列表")
    @GetMapping("/list/{billTypeCode}")
    public Result<List<BillFlowConfig>> listByBillTypeCode(
            @ApiParam("单据类型编码") @PathVariable String billTypeCode) {
        List<BillFlowConfig> list = billFlowConfigService.listByBillTypeCode(billTypeCode);
        return Result.success(list);
    }

    @ApiOperation("获取单据审批流配置详情")
    @GetMapping("/{id}")
    public Result<BillFlowConfig> getById(
            @ApiParam("配置ID") @PathVariable Long id) {
        BillFlowConfig config = billFlowConfigService.getDetail(id);
        return Result.success(config);
    }

    @ApiOperation("创建单据审批流配置")
    @PostMapping
    public Result<BillFlowConfig> create(@RequestBody BillFlowConfig config) {
        BillFlowConfig result = billFlowConfigService.create(config);
        return Result.success(result);
    }

    @ApiOperation("更新单据审批流配置")
    @PutMapping("/{id}")
    public Result<BillFlowConfig> update(
            @ApiParam("配置ID") @PathVariable Long id,
            @RequestBody BillFlowConfig config) {
        config.setId(id);
        BillFlowConfig result = billFlowConfigService.update(config);
        return Result.success(result);
    }

    @ApiOperation("删除单据审批流配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @ApiParam("配置ID") @PathVariable Long id) {
        billFlowConfigService.delete(id);
        return Result.success();
    }

    @ApiOperation("启用单据审批流配置")
    @PostMapping("/{id}/enable")
    public Result<Void> enable(
            @ApiParam("配置ID") @PathVariable Long id) {
        billFlowConfigService.enable(id);
        return Result.success();
    }

    @ApiOperation("禁用单据审批流配置")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(
            @ApiParam("配置ID") @PathVariable Long id) {
        billFlowConfigService.disable(id);
        return Result.success();
    }

    @ApiOperation("设置为默认审批流配置")
    @PostMapping("/{id}/set-default")
    public Result<Void> setDefault(
            @ApiParam("配置ID") @PathVariable Long id) {
        billFlowConfigService.setDefault(id);
        return Result.success();
    }

    @ApiOperation("根据单据类型编码获取审批流编码")
    @GetMapping("/flow-code/{billTypeCode}")
    public Result<String> getFlowCodeByBillTypeCode(
            @ApiParam("单据类型编码") @PathVariable String billTypeCode) {
        String flowCode = billFlowConfigService.getFlowCodeByBillTypeCode(billTypeCode);
        if (flowCode == null) {
            return Result.error("未找到该单据类型对应的审批流配置");
        }
        return Result.success(flowCode);
    }

    @ApiOperation("根据单据类型编码获取审批流配置（用于启动流程）")
    @PostMapping("/flow-config/{billTypeCode}")
    public Result<Map<String, Object>> getFlowConfigByBillTypeCode(
            @ApiParam("单据类型编码") @PathVariable String billTypeCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        Map<String, Object> config = billFlowConfigService.getFlowConfigByBillTypeCode(billTypeCode, variables);
        if (config == null) {
            return Result.error("未找到该单据类型对应的审批流配置");
        }
        return Result.success(config);
    }
}

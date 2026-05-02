package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.BillFlowConfig;
import com.supplychain.entity.BillType;
import com.supplychain.entity.ApprovalFlow;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.BillFlowConfigMapper;
import com.supplychain.mapper.BillTypeMapper;
import com.supplychain.mapper.ApprovalFlowMapper;
import com.supplychain.service.ApprovalFlowService;
import com.supplychain.service.BillFlowConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillFlowConfigServiceImpl extends ServiceImpl<BillFlowConfigMapper, BillFlowConfig> implements BillFlowConfigService {

    private final BillTypeMapper billTypeMapper;
    private final ApprovalFlowMapper approvalFlowMapper;
    private final ApprovalFlowService approvalFlowService;

    @Override
    public Page<BillFlowConfig> pageQuery(Page<BillFlowConfig> page, BillFlowConfig query) {
        LambdaQueryWrapper<BillFlowConfig> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getBillTypeCode())) {
                wrapper.eq(BillFlowConfig::getBillTypeCode, query.getBillTypeCode());
            }
            if (StringUtils.hasText(query.getFlowCode())) {
                wrapper.eq(BillFlowConfig::getFlowCode, query.getFlowCode());
            }
            if (query.getStatus() != null) {
                wrapper.eq(BillFlowConfig::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(BillFlowConfig::getIsDefault).orderByDesc(BillFlowConfig::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<BillFlowConfig> listByBillTypeCode(String billTypeCode) {
        return this.list(new LambdaQueryWrapper<BillFlowConfig>()
                .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                .eq(BillFlowConfig::getStatus, 1)
                .orderByDesc(BillFlowConfig::getIsDefault)
                .orderByDesc(BillFlowConfig::getCreateTime));
    }

    @Override
    public BillFlowConfig getDetail(Long id) {
        BillFlowConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillFlowConfig create(BillFlowConfig config) {
        log.info("创建单据审批流配置: config={}", config);
        
        if (!StringUtils.hasText(config.getBillTypeCode())) {
            throw new BusinessException("单据类型编码不能为空");
        }
        if (!StringUtils.hasText(config.getFlowCode())) {
            throw new BusinessException("审批流编码不能为空");
        }
        
        BillType billType = billTypeMapper.selectOne(
                new LambdaQueryWrapper<BillType>().eq(BillType::getBillTypeCode, config.getBillTypeCode())
        );
        if (billType == null) {
            throw new BusinessException("单据类型不存在");
        }
        config.setBillTypeId(billType.getId());
        
        ApprovalFlow flow = approvalFlowMapper.selectOne(
                new LambdaQueryWrapper<ApprovalFlow>().eq(ApprovalFlow::getFlowCode, config.getFlowCode())
        );
        if (flow == null) {
            throw new BusinessException("审批流不存在");
        }
        config.setFlowId(flow.getId());
        
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            clearDefaultFlag(config.getBillTypeCode());
        } else {
            List<BillFlowConfig> existConfigs = listByBillTypeCode(config.getBillTypeCode());
            if (CollectionUtils.isEmpty(existConfigs)) {
                config.setIsDefault(1);
            } else {
                config.setIsDefault(0);
            }
        }
        
        this.save(config);
        log.info("单据审批流配置创建成功: configId={}", config.getId());
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillFlowConfig update(BillFlowConfig config) {
        log.info("更新单据审批流配置: configId={}", config.getId());
        
        BillFlowConfig existConfig = this.getById(config.getId());
        if (existConfig == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        
        if (StringUtils.hasText(config.getBillTypeCode()) && !config.getBillTypeCode().equals(existConfig.getBillTypeCode())) {
            BillType billType = billTypeMapper.selectOne(
                    new LambdaQueryWrapper<BillType>().eq(BillType::getBillTypeCode, config.getBillTypeCode())
            );
            if (billType == null) {
                throw new BusinessException("单据类型不存在");
            }
            config.setBillTypeId(billType.getId());
        }
        
        if (StringUtils.hasText(config.getFlowCode()) && !config.getFlowCode().equals(existConfig.getFlowCode())) {
            ApprovalFlow flow = approvalFlowMapper.selectOne(
                    new LambdaQueryWrapper<ApprovalFlow>().eq(ApprovalFlow::getFlowCode, config.getFlowCode())
            );
            if (flow == null) {
                throw new BusinessException("审批流不存在");
            }
            config.setFlowId(flow.getId());
        }
        
        if (config.getIsDefault() != null && config.getIsDefault() == 1 && existConfig.getIsDefault() != 1) {
            String billTypeCode = config.getBillTypeCode() != null ? config.getBillTypeCode() : existConfig.getBillTypeCode();
            clearDefaultFlag(billTypeCode);
        }
        
        this.updateById(config);
        log.info("单据审批流配置更新成功: configId={}", config.getId());
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除单据审批流配置: configId={}", id);
        
        BillFlowConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        
        this.removeById(id);
        log.info("单据审批流配置删除成功: configId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        log.info("启用单据审批流配置: configId={}", id);
        
        BillFlowConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        
        config.setStatus(1);
        this.updateById(config);
        log.info("单据审批流配置启用成功: configId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        log.info("禁用单据审批流配置: configId={}", id);
        
        BillFlowConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        
        config.setStatus(0);
        this.updateById(config);
        log.info("单据审批流配置禁用成功: configId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        log.info("设置默认审批流配置: configId={}", id);
        
        BillFlowConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException("单据审批流配置不存在");
        }
        
        if (config.getStatus() != 1) {
            throw new BusinessException("只能设置启用状态的配置为默认");
        }
        
        clearDefaultFlag(config.getBillTypeCode());
        
        config.setIsDefault(1);
        this.updateById(config);
        log.info("设置默认审批流配置成功: configId={}", id);
    }

    private void clearDefaultFlag(String billTypeCode) {
        List<BillFlowConfig> existConfigs = this.list(
                new LambdaQueryWrapper<BillFlowConfig>()
                        .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                        .eq(BillFlowConfig::getIsDefault, 1)
        );
        for (BillFlowConfig existConfig : existConfigs) {
            existConfig.setIsDefault(0);
            this.updateById(existConfig);
        }
    }

    @Override
    public String getFlowCodeByBillTypeCode(String billTypeCode) {
        if (!StringUtils.hasText(billTypeCode)) {
            log.warn("单据类型编码为空");
            return null;
        }
        
        log.info("查找单据类型 [{}] 对应的审批流配置", billTypeCode);
        
        List<BillFlowConfig> allConfigs = this.list(
                new LambdaQueryWrapper<BillFlowConfig>()
                        .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                        .orderByDesc(BillFlowConfig::getIsDefault)
                        .orderByDesc(BillFlowConfig::getCreateTime)
        );
        
        log.info("单据类型 [{}] 共有 {} 个配置记录", billTypeCode, allConfigs.size());
        for (BillFlowConfig cfg : allConfigs) {
            log.info("  - 配置: flowCode={}, status={}, isDefault={}", 
                    cfg.getFlowCode(), cfg.getStatus(), cfg.getIsDefault());
        }
        
        BillFlowConfig config = this.getOne(
                new LambdaQueryWrapper<BillFlowConfig>()
                        .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                        .eq(BillFlowConfig::getStatus, 1)
                        .eq(BillFlowConfig::getIsDefault, 1)
                        .orderByDesc(BillFlowConfig::getCreateTime)
                        .last("LIMIT 1")
        );
        
        if (config != null) {
            log.info("找到默认启用的配置: flowCode={}", config.getFlowCode());
            return config.getFlowCode();
        }
        
        log.warn("未找到默认启用的配置，查找任意启用的配置");
        
        config = this.getOne(
                new LambdaQueryWrapper<BillFlowConfig>()
                        .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                        .eq(BillFlowConfig::getStatus, 1)
                        .orderByDesc(BillFlowConfig::getCreateTime)
                        .last("LIMIT 1")
        );
        
        if (config != null) {
            log.info("找到启用的配置: flowCode={}", config.getFlowCode());
            return config.getFlowCode();
        }
        
        log.error("单据类型 [{}] 未找到任何启用的审批流配置", billTypeCode);
        return null;
    }

    @Override
    public Map<String, Object> getFlowConfigByBillTypeCode(String billTypeCode, Map<String, Object> variables) {
        log.info("开始获取单据类型 [{}] 的审批流配置", billTypeCode);
        
        String flowCode = getFlowCodeByBillTypeCode(billTypeCode);
        if (flowCode == null) {
            log.error("未找到单据类型 [{}] 对应的审批流配置", billTypeCode);
            log.error("请检查：");
            log.error("  1. sc_bill_type 表是否存在 bill_type_code = '{}' 的记录", billTypeCode);
            log.error("  2. sc_bill_flow_config 表是否存在 bill_type_code = '{}' 且 status = 1 的记录", billTypeCode);
            log.error("  3. 对应的审批流是否启用（sc_approval_flow.status = 1）");
            return null;
        }
        
        log.info("根据单据类型 [{}] 找到审批流编码: {}", billTypeCode, flowCode);
        
        Map<String, Object> result = approvalFlowService.getFlowConfigForProcess(flowCode, variables);
        
        if (result == null) {
            log.error("审批流编码 [{}] 未找到有效的审批流配置", flowCode);
            log.error("请检查 sc_approval_flow 表：");
            log.error("  - flow_code = '{}' 的记录是否存在", flowCode);
            log.error("  - status 是否为 1（启用）");
            log.error("  - process_key 是否配置正确（应为 'purchase-order-approval'）");
            return null;
        }
        
        log.info("成功获取审批流配置: flowCode={}", flowCode);
        return result;
    }
}

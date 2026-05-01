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
            return null;
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
            return config.getFlowCode();
        }
        
        config = this.getOne(
                new LambdaQueryWrapper<BillFlowConfig>()
                        .eq(BillFlowConfig::getBillTypeCode, billTypeCode)
                        .eq(BillFlowConfig::getStatus, 1)
                        .orderByDesc(BillFlowConfig::getCreateTime)
                        .last("LIMIT 1")
        );
        
        return config != null ? config.getFlowCode() : null;
    }

    @Override
    public Map<String, Object> getFlowConfigByBillTypeCode(String billTypeCode, Map<String, Object> variables) {
        String flowCode = getFlowCodeByBillTypeCode(billTypeCode);
        if (flowCode == null) {
            log.warn("未找到单据类型 [{}] 对应的审批流配置", billTypeCode);
            return null;
        }
        
        log.info("根据单据类型 [{}] 找到审批流编码: {}", billTypeCode, flowCode);
        return approvalFlowService.getFlowConfigForProcess(flowCode, variables);
    }
}

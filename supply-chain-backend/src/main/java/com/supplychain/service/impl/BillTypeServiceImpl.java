package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.BillType;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.BillTypeMapper;
import com.supplychain.service.BillTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillTypeServiceImpl extends ServiceImpl<BillTypeMapper, BillType> implements BillTypeService {

    @Override
    public Page<BillType> pageQuery(Page<BillType> page, BillType query) {
        LambdaQueryWrapper<BillType> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getBillTypeName())) {
                wrapper.like(BillType::getBillTypeName, query.getBillTypeName());
            }
            if (StringUtils.hasText(query.getBillTypeCode())) {
                wrapper.eq(BillType::getBillTypeCode, query.getBillTypeCode());
            }
            if (StringUtils.hasText(query.getModuleName())) {
                wrapper.eq(BillType::getModuleName, query.getModuleName());
            }
            if (query.getStatus() != null) {
                wrapper.eq(BillType::getStatus, query.getStatus());
            }
        }
        wrapper.orderByAsc(BillType::getSortOrder).orderByDesc(BillType::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<BillType> listAll() {
        return this.list(new LambdaQueryWrapper<BillType>()
                .eq(BillType::getStatus, 1)
                .orderByAsc(BillType::getSortOrder)
                .orderByDesc(BillType::getCreateTime));
    }

    @Override
    public BillType getDetail(Long id) {
        BillType billType = this.getById(id);
        if (billType == null) {
            throw new BusinessException("单据类型不存在");
        }
        return billType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillType create(BillType billType) {
        log.info("创建单据类型: billType={}", billType);
        
        LambdaQueryWrapper<BillType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillType::getBillTypeCode, billType.getBillTypeCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("单据类型编码已存在");
        }
        
        if (billType.getStatus() == null) {
            billType.setStatus(1);
        }
        if (billType.getSortOrder() == null) {
            billType.setSortOrder(1);
        }
        
        this.save(billType);
        log.info("单据类型创建成功: billTypeId={}", billType.getId());
        return billType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillType update(BillType billType) {
        log.info("更新单据类型: billTypeId={}", billType.getId());
        
        BillType existBillType = this.getById(billType.getId());
        if (existBillType == null) {
            throw new BusinessException("单据类型不存在");
        }
        
        LambdaQueryWrapper<BillType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BillType::getBillTypeCode, billType.getBillTypeCode());
        wrapper.ne(BillType::getId, billType.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("单据类型编码已存在");
        }
        
        this.updateById(billType);
        log.info("单据类型更新成功: billTypeId={}", billType.getId());
        return billType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除单据类型: billTypeId={}", id);
        
        BillType billType = this.getById(id);
        if (billType == null) {
            throw new BusinessException("单据类型不存在");
        }
        
        this.removeById(id);
        log.info("单据类型删除成功: billTypeId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(Long id) {
        log.info("启用单据类型: billTypeId={}", id);
        
        BillType billType = this.getById(id);
        if (billType == null) {
            throw new BusinessException("单据类型不存在");
        }
        
        billType.setStatus(1);
        this.updateById(billType);
        log.info("单据类型启用成功: billTypeId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long id) {
        log.info("禁用单据类型: billTypeId={}", id);
        
        BillType billType = this.getById(id);
        if (billType == null) {
            throw new BusinessException("单据类型不存在");
        }
        
        billType.setStatus(0);
        this.updateById(billType);
        log.info("单据类型禁用成功: billTypeId={}", id);
    }
}

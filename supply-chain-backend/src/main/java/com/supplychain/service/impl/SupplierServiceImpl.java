package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.Supplier;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.SupplierMapper;
import com.supplychain.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public Page<Supplier> pageQuery(Page<Supplier> page, Supplier query) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getSupplierName())) {
                wrapper.like(Supplier::getSupplierName, query.getSupplierName());
            }
            if (StringUtils.hasText(query.getSupplierCode())) {
                wrapper.like(Supplier::getSupplierCode, query.getSupplierCode());
            }
            if (query.getStatus() != null) {
                wrapper.eq(Supplier::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(Supplier::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Supplier> listAll() {
        return this.list(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getStatus, 1)
                .orderByDesc(Supplier::getCreateTime));
    }

    @Override
    public Supplier getDetail(Long id) {
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        return supplier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supplier create(Supplier supplier) {
        log.info("创建供应商: supplier={}", supplier);
        
        if (supplier.getStatus() == null) {
            supplier.setStatus(1);
        }
        
        this.save(supplier);
        log.info("供应商创建成功: supplierId={}", supplier.getId());
        return supplier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Supplier update(Supplier supplier) {
        log.info("更新供应商: supplierId={}", supplier.getId());
        
        Supplier existSupplier = this.getById(supplier.getId());
        if (existSupplier == null) {
            throw new BusinessException("供应商不存在");
        }
        
        this.updateById(supplier);
        log.info("供应商更新成功: supplierId={}", supplier.getId());
        return supplier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除供应商: supplierId={}", id);
        
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        
        this.removeById(id);
        log.info("供应商删除成功: supplierId={}", id);
    }
}

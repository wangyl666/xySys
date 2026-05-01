package com.supplychain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supplychain.entity.Material;
import com.supplychain.exception.BusinessException;
import com.supplychain.mapper.MaterialMapper;
import com.supplychain.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Override
    public Page<Material> pageQuery(Page<Material> page, Material query) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getMaterialName())) {
                wrapper.like(Material::getMaterialName, query.getMaterialName());
            }
            if (StringUtils.hasText(query.getMaterialCode())) {
                wrapper.like(Material::getMaterialCode, query.getMaterialCode());
            }
            if (query.getStatus() != null) {
                wrapper.eq(Material::getStatus, query.getStatus());
            }
        }
        wrapper.orderByDesc(Material::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Material> listAll() {
        return this.list(new LambdaQueryWrapper<Material>()
                .eq(Material::getStatus, 1)
                .orderByDesc(Material::getCreateTime));
    }

    @Override
    public Material getDetail(Long id) {
        Material material = this.getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        return material;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Material create(Material material) {
        log.info("创建物料: material={}", material);
        
        if (material.getStatus() == null) {
            material.setStatus(1);
        }
        
        this.save(material);
        log.info("物料创建成功: materialId={}", material.getId());
        return material;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Material update(Material material) {
        log.info("更新物料: materialId={}", material.getId());
        
        Material existMaterial = this.getById(material.getId());
        if (existMaterial == null) {
            throw new BusinessException("物料不存在");
        }
        
        this.updateById(material);
        log.info("物料更新成功: materialId={}", material.getId());
        return material;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除物料: materialId={}", id);
        
        Material material = this.getById(id);
        if (material == null) {
            throw new BusinessException("物料不存在");
        }
        
        this.removeById(id);
        log.info("物料删除成功: materialId={}", id);
    }
}

package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.Material;

import java.util.List;

public interface MaterialService extends IService<Material> {

    /**
     * 分页查询物料
     */
    Page<Material> pageQuery(Page<Material> page, Material query);

    /**
     * 查询所有物料列表
     */
    List<Material> listAll();

    /**
     * 根据ID获取物料详情
     */
    Material getDetail(Long id);

    /**
     * 创建物料
     */
    Material create(Material material);

    /**
     * 更新物料
     */
    Material update(Material material);

    /**
     * 删除物料
     */
    void delete(Long id);
}

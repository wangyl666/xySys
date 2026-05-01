package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.Supplier;

import java.util.List;

public interface SupplierService extends IService<Supplier> {

    /**
     * 分页查询供应商
     */
    Page<Supplier> pageQuery(Page<Supplier> page, Supplier query);

    /**
     * 查询所有供应商列表
     */
    List<Supplier> listAll();

    /**
     * 根据ID获取供应商详情
     */
    Supplier getDetail(Long id);

    /**
     * 创建供应商
     */
    Supplier create(Supplier supplier);

    /**
     * 更新供应商
     */
    Supplier update(Supplier supplier);

    /**
     * 删除供应商
     */
    void delete(Long id);
}

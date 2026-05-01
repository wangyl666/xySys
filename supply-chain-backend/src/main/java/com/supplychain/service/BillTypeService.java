package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.BillType;

import java.util.List;

public interface BillTypeService extends IService<BillType> {

    Page<BillType> pageQuery(Page<BillType> page, BillType query);

    List<BillType> listAll();

    BillType getDetail(Long id);

    BillType create(BillType billType);

    BillType update(BillType billType);

    void delete(Long id);

    void enable(Long id);

    void disable(Long id);
}

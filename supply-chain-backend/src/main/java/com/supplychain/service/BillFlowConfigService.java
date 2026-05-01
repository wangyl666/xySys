package com.supplychain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supplychain.entity.BillFlowConfig;

import java.util.List;
import java.util.Map;

public interface BillFlowConfigService extends IService<BillFlowConfig> {

    Page<BillFlowConfig> pageQuery(Page<BillFlowConfig> page, BillFlowConfig query);

    List<BillFlowConfig> listByBillTypeCode(String billTypeCode);

    BillFlowConfig getDetail(Long id);

    BillFlowConfig create(BillFlowConfig config);

    BillFlowConfig update(BillFlowConfig config);

    void delete(Long id);

    void enable(Long id);

    void disable(Long id);

    void setDefault(Long id);

    String getFlowCodeByBillTypeCode(String billTypeCode);

    Map<String, Object> getFlowConfigByBillTypeCode(String billTypeCode, Map<String, Object> variables);
}

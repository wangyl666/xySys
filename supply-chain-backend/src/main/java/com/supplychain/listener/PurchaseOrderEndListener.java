package com.supplychain.listener;

import com.supplychain.entity.PurchaseOrder;
import com.supplychain.mapper.PurchaseOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PurchaseOrderEndListener implements ExecutionListener {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public void notify(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();
        Boolean approved = (Boolean) execution.getVariable("approved");

        log.info("流程结束: processInstanceId={}, businessKey={}, approved={}",
                processInstanceId, businessKey, approved);

        PurchaseOrder order = purchaseOrderMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PurchaseOrder>()
                        .eq(PurchaseOrder::getProcessInstanceId, processInstanceId)
        );

        if (order != null) {
            if (Boolean.TRUE.equals(approved)) {
                order.setApprovalStatus("APPROVED");
                order.setOrderStatus("APPROVED");
            } else {
                order.setApprovalStatus("REJECTED");
                order.setOrderStatus("REJECTED");
            }
            purchaseOrderMapper.updateById(order);
            log.info("订单状态已更新: orderId={}, approvalStatus={}",
                    order.getId(), order.getApprovalStatus());
        }
    }
}

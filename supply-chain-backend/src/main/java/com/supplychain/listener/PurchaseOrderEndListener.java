package com.supplychain.listener;

import com.supplychain.entity.PurchaseOrder;
import com.supplychain.mapper.PurchaseOrderMapper;
import com.supplychain.util.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PurchaseOrderEndListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();
        Boolean approved = (Boolean) execution.getVariable("approved");

        log.info("流程结束: processInstanceId={}, businessKey={}, approved={}",
                processInstanceId, businessKey, approved);

        if (SpringContextHolder.getApplicationContext() == null) {
            log.error("Spring 上下文未初始化，无法更新订单状态");
            return;
        }

        PurchaseOrderMapper purchaseOrderMapper = SpringContextHolder.getBean(PurchaseOrderMapper.class);

        if (purchaseOrderMapper == null) {
            log.error("无法获取 PurchaseOrderMapper Bean，无法更新订单状态");
            return;
        }

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
        } else {
            log.warn("未找到订单: processInstanceId={}", processInstanceId);
        }
    }
}

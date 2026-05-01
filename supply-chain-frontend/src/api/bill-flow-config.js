import request from '@/utils/request'

export function getBillFlowConfigPage(params) {
  return request({
    url: '/api/bill-flow-config/page',
    method: 'get',
    params
  })
}

export function getBillFlowConfigListByBillTypeCode(billTypeCode) {
  return request({
    url: `/api/bill-flow-config/list/${billTypeCode}`,
    method: 'get'
  })
}

export function getBillFlowConfig(id) {
  return request({
    url: `/api/bill-flow-config/${id}`,
    method: 'get'
  })
}

export function createBillFlowConfig(data) {
  return request({
    url: '/api/bill-flow-config',
    method: 'post',
    data
  })
}

export function updateBillFlowConfig(id, data) {
  return request({
    url: `/api/bill-flow-config/${id}`,
    method: 'put',
    data
  })
}

export function deleteBillFlowConfig(id) {
  return request({
    url: `/api/bill-flow-config/${id}`,
    method: 'delete'
  })
}

export function enableBillFlowConfig(id) {
  return request({
    url: `/api/bill-flow-config/${id}/enable`,
    method: 'post'
  })
}

export function disableBillFlowConfig(id) {
  return request({
    url: `/api/bill-flow-config/${id}/disable`,
    method: 'post'
  })
}

export function setDefaultBillFlowConfig(id) {
  return request({
    url: `/api/bill-flow-config/${id}/set-default`,
    method: 'post'
  })
}

export function getFlowCodeByBillTypeCode(billTypeCode) {
  return request({
    url: `/api/bill-flow-config/flow-code/${billTypeCode}`,
    method: 'get'
  })
}

export function getFlowConfigByBillTypeCode(billTypeCode, variables) {
  return request({
    url: `/api/bill-flow-config/flow-config/${billTypeCode}`,
    method: 'post',
    data: variables
  })
}

import request from '@/utils/request'

export function getPurchaseOrderPage(params) {
  return request({
    url: '/api/purchase-order/page',
    method: 'get',
    params
  })
}

export function submitPurchaseOrderWithFlowConfig(id, flowConfigId) {
  return request({
    url: `/api/purchase-order/${id}/submit`,
    method: 'post',
    params: flowConfigId ? { flowConfigId } : {}
  })
}

export function getPurchaseOrderById(id) {
  return request({
    url: `/api/purchase-order/${id}`,
    method: 'get'
  })
}

export function getPurchaseOrderItems(id) {
  return request({
    url: `/api/purchase-order/${id}/items`,
    method: 'get'
  })
}

export function createPurchaseOrder(data) {
  return request({
    url: '/api/purchase-order',
    method: 'post',
    data
  })
}

export function updatePurchaseOrder(data) {
  return request({
    url: `/api/purchase-order/${data.id}`,
    method: 'put',
    data
  })
}

export function submitPurchaseOrder(id) {
  return request({
    url: `/api/purchase-order/${id}/submit`,
    method: 'post'
  })
}

export function deletePurchaseOrder(id) {
  return request({
    url: `/api/purchase-order/${id}`,
    method: 'delete'
  })
}

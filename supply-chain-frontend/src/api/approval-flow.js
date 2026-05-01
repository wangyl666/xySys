import request from '@/utils/request'

export function getApprovalFlowPage(params) {
  return request({
    url: '/api/approval-flow/page',
    method: 'get',
    params
  })
}

export function getApprovalFlow(id) {
  return request({
    url: `/api/approval-flow/${id}`,
    method: 'get'
  })
}

export function createApprovalFlow(data) {
  return request({
    url: '/api/approval-flow',
    method: 'post',
    data
  })
}

export function updateApprovalFlow(id, data) {
  return request({
    url: `/api/approval-flow/${id}`,
    method: 'put',
    data
  })
}

export function deleteApprovalFlow(id) {
  return request({
    url: `/api/approval-flow/${id}`,
    method: 'delete'
  })
}

export function enableApprovalFlow(id) {
  return request({
    url: `/api/approval-flow/${id}/enable`,
    method: 'post'
  })
}

export function disableApprovalFlow(id) {
  return request({
    url: `/api/approval-flow/${id}/disable`,
    method: 'post'
  })
}

export function getApprovalNodes(flowId) {
  return request({
    url: `/api/approval-flow/${flowId}/nodes`,
    method: 'get'
  })
}

export function createApprovalNode(data) {
  return request({
    url: '/api/approval-flow/node',
    method: 'post',
    data
  })
}

export function updateApprovalNode(id, data) {
  return request({
    url: `/api/approval-flow/node/${id}`,
    method: 'put',
    data
  })
}

export function deleteApprovalNode(id) {
  return request({
    url: `/api/approval-flow/node/${id}`,
    method: 'delete'
  })
}

export function getApprovalFlowConfig(flowCode) {
  return request({
    url: `/api/approval-flow/config/${flowCode}`,
    method: 'get'
  })
}

import request from '@/utils/request'

export function getBillTypePage(params) {
  return request({
    url: '/api/bill-type/page',
    method: 'get',
    params
  })
}

export function getBillTypeList() {
  return request({
    url: '/api/bill-type/list',
    method: 'get'
  })
}

export function getBillType(id) {
  return request({
    url: `/api/bill-type/${id}`,
    method: 'get'
  })
}

export function createBillType(data) {
  return request({
    url: '/api/bill-type',
    method: 'post',
    data
  })
}

export function updateBillType(id, data) {
  return request({
    url: `/api/bill-type/${id}`,
    method: 'put',
    data
  })
}

export function deleteBillType(id) {
  return request({
    url: `/api/bill-type/${id}`,
    method: 'delete'
  })
}

export function enableBillType(id) {
  return request({
    url: `/api/bill-type/${id}/enable`,
    method: 'post'
  })
}

export function disableBillType(id) {
  return request({
    url: `/api/bill-type/${id}/disable`,
    method: 'post'
  })
}

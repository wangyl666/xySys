import request from '@/utils/request'

export function getSupplierPage(params) {
  return request({
    url: '/api/supplier/page',
    method: 'get',
    params
  })
}

export function getSupplierList() {
  return request({
    url: '/api/supplier/list',
    method: 'get'
  })
}

export function getSupplierById(id) {
  return request({
    url: `/api/supplier/${id}`,
    method: 'get'
  })
}

export function createSupplier(data) {
  return request({
    url: '/api/supplier',
    method: 'post',
    data
  })
}

export function updateSupplier(data) {
  return request({
    url: `/api/supplier/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteSupplier(id) {
  return request({
    url: `/api/supplier/${id}`,
    method: 'delete'
  })
}

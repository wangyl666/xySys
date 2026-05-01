import request from '@/utils/request'

export function getMaterialPage(params) {
  return request({
    url: '/api/material/page',
    method: 'get',
    params
  })
}

export function getMaterialList() {
  return request({
    url: '/api/material/list',
    method: 'get'
  })
}

export function getMaterialById(id) {
  return request({
    url: `/api/material/${id}`,
    method: 'get'
  })
}

export function createMaterial(data) {
  return request({
    url: '/api/material',
    method: 'post',
    data
  })
}

export function updateMaterial(data) {
  return request({
    url: `/api/material/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteMaterial(id) {
  return request({
    url: `/api/material/${id}`,
    method: 'delete'
  })
}

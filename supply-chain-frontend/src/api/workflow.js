import request from '@/utils/request'

export function getTodoTasks(userId) {
  return request({
    url: `/api/workflow/todo/${userId}`,
    method: 'get'
  })
}

export function getDoneTasks(userId) {
  return request({
    url: `/api/workflow/done/${userId}`,
    method: 'get'
  })
}

export function claimTask(taskId, userId) {
  return request({
    url: `/api/workflow/claim/${taskId}/${userId}`,
    method: 'post'
  })
}

export function unclaimTask(taskId) {
  return request({
    url: `/api/workflow/unclaim/${taskId}`,
    method: 'post'
  })
}

export function approveTask(taskId, comment) {
  return request({
    url: `/api/workflow/approve/${taskId}`,
    method: 'post',
    params: { comment }
  })
}

export function rejectTask(taskId, comment) {
  return request({
    url: `/api/workflow/reject/${taskId}`,
    method: 'post',
    params: { comment }
  })
}

export function completeTask(taskId, variables) {
  return request({
    url: `/api/workflow/complete/${taskId}`,
    method: 'post',
    data: variables
  })
}

export function transferTask(taskId, userId) {
  return request({
    url: `/api/workflow/transfer/${taskId}/${userId}`,
    method: 'post'
  })
}

export function delegateTask(taskId, userId) {
  return request({
    url: `/api/workflow/delegate/${taskId}/${userId}`,
    method: 'post'
  })
}

export function getHistoryTasks(processInstanceId) {
  return request({
    url: `/api/workflow/history/${processInstanceId}`,
    method: 'get'
  })
}

export function getActiveActivities(processInstanceId) {
  return request({
    url: `/api/workflow/activities/${processInstanceId}`,
    method: 'get'
  })
}

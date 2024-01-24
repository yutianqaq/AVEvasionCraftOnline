import request from '../utils/request.js'

export function fetchConfig() {
  return request({
    url: `/api/avevasion/config`,
    method: 'get'
  })
}

export function fetchDownloadLink(endpoint) {
  return request({
  url: `/api${endpoint}`,
  method: 'get',
  responseType: 'blob'
})
}

export function fetchCompileUpload(data) {
  return request({
    url: `/api/compiler`,
    method: 'post',
    data,
  })
}

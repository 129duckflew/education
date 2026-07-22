
# 后台管理: 登录相关接口
## 后台登录 登录之后返回token
**URL:** `/admin/login`

**Type:** `POST`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 后台登录只需要账号和密码 其他的信息不需要




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
username|string|用户名|true|-
password|string|密码|true|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /admin/login --data '{
  "username": "christopher.kreiger",
  "password": "tdclb0"
}'
```

**Response-example:**
```
{
  "code": 200,
  "msg": "ok",
  "data": {
    "tokenName": "satoken",
    "tokenValue": "99c39726-3324-4204-9449-4dff06510090",
    "isLogin": true,
    "loginId": "1",
    "loginType": "login",
    "tokenTimeout": 2592000,
    "sessionTimeout": 2592000,
    "tokenSessionTimeout": -2,
    "tokenActivityTimeout": -1,
    "loginDevice": "default-device"
  }
}
```



# 后台管理: 管理员用户管理
## 获取当前管理员用户的菜单列表
**URL:** `/admin/menu_list`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 获取当前管理员用户的菜单列表





**Request-example:**
```
curl -X GET -i /admin/menu_list
```

**Response-example:**
```
{}
```

## 
**URL:** `/admin/role`

**Type:** `POST`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
roleIdList|array|角色Id|true|-
adminId|int32|管理员id|true|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /admin/role --data '{
  "roleIdList": [
    457
  ],
  "adminId": 863
}'
```

**Response-example:**
```
{}
```


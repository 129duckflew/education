
# 角色相关接口
## 添加角色
**URL:** `/role/`

**Type:** `POST`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 添加角色




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
roleName|string|角色名|true|-
permissionIdList|array|No comments found.|true|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /role/ --data '{
  "roleName": "christopher.kreiger",
  "permissionIdList": [
    729
  ]
}'
```

**Response-example:**
```
{}
```

## 
**URL:** `/role/{roleId}`

**Type:** `DELETE`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
roleId|int32|No comments found.|true|-



**Request-example:**
```
curl -X DELETE -i /role/954
```

**Response-example:**
```
{}
```

## 
**URL:** `/role/{roleId}`

**Type:** `PUT`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
roleName|string|角色名|true|-
permissionIdList|array|No comments found.|true|-
roleId|int32|No comments found.|true|-

**Request-example:**
```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /role/{roleId} --data '{
  "roleName": "christopher.kreiger",
  "permissionIdList": [
    626
  ],
  "roleId": 63
}'
```

**Response-example:**
```
{}
```


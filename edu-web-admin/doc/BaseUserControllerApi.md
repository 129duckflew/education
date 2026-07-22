
# 
## 分页查询用户列表
**URL:** `/user/`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 分页查询用户列表



**Query-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
pageNum|int32|当前分页数，如查询第一页,则pageNum则为0|true|-
pageSize|int32|分页大小 默认值5|true|-


**Request-example:**
```
curl -X GET -i /user/?pageNum=0&pageSize=5 --data '"5"'
```

**Response-example:**
```
{
  "code": 200,
  "msg": "",
  "data": {
    "total": 0,
    "userList": []
  }
}
```


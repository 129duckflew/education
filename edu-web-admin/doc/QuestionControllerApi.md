
# 问题相关接口
## 分页获取所有需要审核的问题
**URL:** `/question/`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 分页获取所有需要审核的问题



**Query-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
pageNum|int32|分页数(0为第一页)|true|-
pageSize|int32|分页大小|true|-


**Request-example:**
```
curl -X GET -i /question/?pageNum=541&pageSize=10
```

**Response-example:**
```
{}
```

## 对问题进行封禁
**URL:** `/question/ban/{qId}`

**Type:** `POST`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 对问题进行封禁


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
qId|int32|No comments found.|true|-



**Request-example:**
```
curl -X POST -i /question/ban/902
```

**Response-example:**
```
{}
```

## 管理员对问题进行审核
**URL:** `/question/audit`

**Type:** `POST`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 管理员对问题进行审核




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
questionId|int32|问题的id|true|-
questionStatus|int32|问题状态  0为禁止访问(指的是特殊情况下对已经通过的问题进行封禁) 1为正常访问 2为问题审核不通过|true|-
comment|string|备注|false|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /question/audit --data '{
  "questionId": 970,
  "questionStatus": 335,
  "comment": "td8r0m"
}'
```

**Response-example:**
```
{}
```


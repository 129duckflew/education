
# 回答相关接口
## 对回答进行封禁
**URL:** `/answer/ban/{ansId}`

**Type:** `POST`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 对回答进行封禁


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
ansId|int32|No comments found.|true|-



**Request-example:**
```
curl -X POST -i /answer/ban/986
```

**Response-example:**
```
{}
```


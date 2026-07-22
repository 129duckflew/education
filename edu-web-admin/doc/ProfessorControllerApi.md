
# 
## 获取所有待审核教授
**URL:** `/professor/professor/all_pre`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 获取所有待审核教授





**Request-example:**
```
curl -X GET -i /professor/professor/all_pre
```

**Response-example:**
```
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "id": 5,
      "username": "duckflew",
      "password": "e10adc3949ba59abbe56e057f20f883e",
      "email": "129duckflew@gmail.com",
      "birthday": "2001-01-10",
      "gender": "男",
      "nickName": "李亮",
      "cardId": "421222200101100014",
      "userType": 2,
      "realName": "李亮",
      "cvFileName": "70de302e-11be-47c9-9e79-a78e34c1bb3d.docx"
    }
  ]
}
```

## 通过教授审核
**URL:** `/professor/access/{userId}`

**Type:** `POST`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 通过教授审核


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
userId|int32|用户Id|true|-



**Request-example:**
```
curl -X POST -i /professor/access/576
```

**Response-example:**
```
{
  "code": 200,
  "msg": "教授申请审核通过"
}
```

## 添加教授简介(暂未实现)
**URL:** `/professor/introduction`

**Type:** `POST`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 添加教授简介(暂未实现)





**Request-example:**
```
curl -X POST -i /professor/introduction
```

**Response-example:**
```
{}
```


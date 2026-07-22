
# 后台管理: 学信网数据相关接口
## 通过userId查询学信网的学历信息
**URL:** `/edu_datasource/{userId}`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 通过userId查询学信网的学历信息


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
userId|int32|用户ID|true|-



**Request-example:**
```
curl -X GET -i /edu_datasource/310
```

**Response-example:**
```
{
  "code": 200,
  "msg": "获取学信网数据成功",
  "data": {
    "baseInfo": {
      "id": 1,
      "cardId": "422324196006090014",
      "realName": "李亮",
      "birthday": "2001/3/23 上午12:00",
      "birthPlace": "湖北省黄冈市",
      "topEdu": "本科",
      "topEduLevel": "学士",
      "isAllDay": 1
    },
    "experience": [
      {
        "id": 1,
        "eduDataSourceId": 1,
        "startTime": "2019/9/1 下午2:42",
        "endTime": "2023/6/15 下午2:42",
        "schoolName": "武汉科技大学",
        "eduLevel": "学士",
        "isAllDay": 1
      }
    ]
  }
}
```


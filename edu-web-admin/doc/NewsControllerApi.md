
# 
## 上传资讯文章封面
**URL:** `/news/cover/upload`

**Type:** `POST`


**Content-Type:** `multipart/form-data`

**Description:** 调用这个接口会上传头像文件并且并且直接修改用户头像<br>获取资讯封面的方法: 通过获取资讯详情获取,得到cover<br>头像的访问地址 CHANGE_ME/oss/news/cover/{cover字段的值}



**Query-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
file|file|封面文件|true|-


**Request-example:**
```
curl -X POST -H 'Content-Type: multipart/form-data' -i /news/cover/upload
```

**Response-example:**
```
{}
```

## 更新资讯信息
**URL:** `/news/`

**Type:** `PUT`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 需要更新什么字段就填什么，不更新的为null，id必填，并且要求有效，后端会校验




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
id|int32|资讯id|true|-
newsTitle|string|资讯标题|false|-
newsContent|string|资讯正文|false|-
priority|int32|首页置顶优先级 数字越小排在越上面|false|-
cover|string|封面文件的地址(只要文件名)|false|-
indexShow|int32|是否首页展示 0=false,1=true|false|-

**Request-example:**
```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /news/ --data '{
  "id": 838,
  "newsTitle": "lhebu9",
  "newsContent": "w7080k",
  "priority": 211,
  "cover": "hcltqt",
  "indexShow": 188
}'
```

**Response-example:**
```
{}
```

## 
**URL:** `/news/`

**Type:** `POST`


**Content-Type:** `application/json; charset=utf-8`

**Description:** 




**Body-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
newsTitle|string|资讯标题|true|-
newsContent|string|资讯正文|true|-
priority|int32|首页置顶优先级 数字越小排在越上面|true|-
cover|string|封面文件的地址(只要文件名)|true|-
indexShow|int32|是否首页展示 0=false,1=true|true|-

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /news/ --data '{
  "newsTitle": "i877i5",
  "newsContent": "w93dt1",
  "priority": 433,
  "cover": "3a3jly",
  "indexShow": 950
}'
```

**Response-example:**
```
{}
```

## 
**URL:** `/news/{newsId}`

**Type:** `DELETE`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
newsId|int32|No comments found.|true|-



**Request-example:**
```
curl -X DELETE -i /news/216
```

**Response-example:**
```
{}
```


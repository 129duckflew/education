
# 后台管理: 菜单相关接口
## 获取菜单所需的权限
**URL:** `/menu/permission/{menuId}`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 获取菜单所需的权限


**Path-parameters:**

Parameter|Type|Description|Required|Since
---|---|---|---|---
menuId|int32|菜单id|true|-



**Request-example:**
```
curl -X GET -i /menu/permission/150
```

**Response-example:**
```
{}
```

## 获取所有菜单以及菜单下的权限
**URL:** `/menu/`

**Type:** `GET`


**Content-Type:** `application/x-www-form-urlencoded;charset=utf-8`

**Description:** 获取所有菜单以及菜单下的权限





**Request-example:**
```
curl -X GET -i /menu/
```

**Response-example:**
```
{}
```


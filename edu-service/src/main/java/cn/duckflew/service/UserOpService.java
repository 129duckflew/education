package cn.duckflew.service;

import cn.duckflew.entity.user.UserOp;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.mapper.UserOpMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class UserOpService extends ServiceImpl<UserOpMapper, UserOp>
{

    @Autowired
    UserOpMapper userOpMapper;
    @Transactional(rollbackFor = Exception.class)
    public void addOp(UserOpType opType, Integer userId, Integer resourceId,Integer opStatus)
    {
        UserOp op=userOpMapper.selectOne(
                new QueryWrapper<UserOp>()
                        .eq("op_type", opType.getCode())
                        .eq("user_id",userId)
                        .eq("resource_id",resourceId)
        );
        if (op==null)
        {
            UserOp userOp = new UserOp();
            userOp.setOpType(opType.getCode());
            userOp.setOpStatus(opStatus);
            userOp.setUserId(userId);
            userOp.setResourceId(resourceId);
            userOpMapper.insert(userOp);
        }
        else
        {
            op.setOpStatus(opStatus);
            userOpMapper.updateById(op);
        }
    }
}

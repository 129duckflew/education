package cn.duckflew.cron;

import cn.duckflew.entity.user.UserOp;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.service.UserOpService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RedisToMysql
{

    /**
     * 答案点赞数据同步
     */
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    UserOpService userOpService;
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void LikeData()
    {
        HashOperations<String, String, Integer> opsForHash = redisTemplate.opsForHash();
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        Set<Integer> idList= Objects.requireNonNull(opsForSet.members("answer:be_liked_list")).stream().map(id-> (Integer)id).collect(Collectors.toSet());
        for (Integer answerId : idList)
        {
            Map<String, Integer> userIdMapToStatus = opsForHash.entries("answer:like:" + answerId);
            for (Integer userId:userIdMapToStatus.keySet().stream().map(Integer::parseInt).collect(Collectors.toSet()))
            {
                UserOp op=userOpService.getOne(
                        new QueryWrapper<UserOp>()
                                .eq("op_type", UserOpType.LIKE_ANSWER.getCode())
                                .eq("user_id",userId)
                                .eq("resource_id",answerId)
                );
                Integer opStatus=userIdMapToStatus.get(userId.toString());
                if (op==null)
                {
                    UserOp userOp = new UserOp();
                    userOp.setResourceId(answerId);
                    userOp.setUserId(userId);
                    userOp.setOpType(UserOpType.LIKE_ANSWER.getCode());
                    userOp.setOpStatus(opStatus);
                    userOpService.save(userOp);
                    log.debug("插入点赞记录{}",userOp);
                }
                else
                {
                    op.setOpStatus(opStatus);
                    userOpService.updateById(op);
                }
            }
        }
    }
}

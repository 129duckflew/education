package cn.duckflew.service;

import cn.duckflew.entity.LoginLog;
import cn.duckflew.enums.LoginType;
import cn.duckflew.mapper.LoginLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LoginLogService
{

    @Autowired
    LoginLogMapper loginLogMapper;

    @Transactional(rollbackFor =Exception.class)
    public void addLoginLog(Integer userId, LoginType loginType,String ip,String loginDevice)
    {
        log.info("登录后写入日志");
        LoginLog loginLog = new LoginLog();
        loginLog.setLastLoginTime(new Date());
        loginLog.setLoginType(loginType.getCode());
        loginLog.setUserId(userId);
        loginLog.setLoginIp(ip);
        loginLog.setLoginDevice(loginDevice);
        loginLogMapper.insert(loginLog);
    }

    public LoginLog lastLoginInfo(int userId)
    {
        List<LoginLog> list = loginLogMapper.selectList(new QueryWrapper<LoginLog>().eq("user_id", userId).orderByDesc("last_login_time"));
        if (list==null||list.isEmpty()) return null;
        return list.get(0);
    }

    public Integer getActiveNum(Date startTime, Date endTime)
    {
        return loginLogMapper.selectCount(
                new QueryWrapper<LoginLog>()
                .ge("last_login_time",startTime)
                .le("last_login_time",endTime)
        );
    }
}

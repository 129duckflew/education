package cn.duckflew.config;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.duckflew.enums.LoginType;
import cn.duckflew.service.LoginLogService;
import cn.duckflew.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * 事件监听器
 */
@Component
@Slf4j
public class MySaTokenListener implements SaTokenListener
{
    @Autowired
    HttpServletRequest request;
    @Autowired
    LoginLogService loginLogService;
    @Override
    public void doLogin(String loginDevice ,Object loginId, String tokenValue, SaLoginModel loginModel)
    {
        String ip = IPUtil.getIpAddr(request);
        LoginType loginType = (LoginType) loginModel.getExtra("loginType");
        loginLogService.addLoginLog(Integer.parseInt(loginId.toString()),loginType,ip,loginModel.getDevice());
    }

    @Override
    public void doLogout(String s, Object o, String s1)
    {

    }

    @Override
    public void doKickout(String s, Object o, String s1)
    {

    }

    @Override
    public void doReplaced(String s, Object o, String s1)
    {

    }

    @Override
    public void doDisable(String s, Object o, long l)
    {

    }

    @Override
    public void doUntieDisable(String s, Object o)
    {

    }

    @Override
    public void doCreateSession(String s)
    {
        log.info("事件监听器: createSession");
    }

    @Override
    public void doLogoutSession(String s)
    {

    }
}

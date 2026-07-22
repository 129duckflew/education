package cn.duckflew.aspect;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.NoRepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Component
@Aspect
@Slf4j
public class NoRepeatSubmitAop {

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around("pointCut(noRepeatSubmit)")
    public Object arround(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            SaSession userSession = StpUtil.getSession(true);
            String key = StpUtil.getLoginIdAsString() + "-" + request.getServletPath();

            if (userSession.get(key) == null) {
                // 如果缓存中有这个url视为重复提交
                Object o = pjp.proceed();
                userSession.set(key, 0);
                return o;
            } else {
                log.error("重复请求，请稍后在试试。"+request.getServletPath());
                return SaResult.ok().setMsg("重复请求，请稍后在试试");
            }
        }catch (NotLoginException notLoginException)
        {
            log.warn("notlogin info:{}",notLoginException.getMessage());
            return SaResult.error().setCode(401).setMsg(notLoginException.getMessage());
        }
        catch (Throwable e) {
            e.printStackTrace();
            log.error("验证重复提交时出现未知异常!");
            return SaResult.error().setMsg("验证重复提交时出现未知异常").setCode(500);
        }
    }
}

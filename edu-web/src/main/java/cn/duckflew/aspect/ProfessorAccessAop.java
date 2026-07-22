package cn.duckflew.aspect;


import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.annotation.NoRepeatSubmit;
import cn.duckflew.annotation.ProfessorAccess;
import cn.duckflew.service.BaseUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ProfessorAccessAop
{
    @Pointcut("@annotation(professorAccess)")
    public void pointCut(ProfessorAccess professorAccess) {
    }


    @Autowired
    BaseUserService baseUserService;
    @Around("pointCut(professorAccess)")
    public Object around(ProceedingJoinPoint pjp, ProfessorAccess professorAccess) throws Throwable
    {
//        try {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = attributes.getRequest();
          if (!baseUserService.isPro(StpUtil.getLoginIdAsInt()))
              throw  new NotPermissionException("教授");
          return pjp.proceed();
//        catch (NotLoginException notLoginException)
//    {
//        log.warn("notlogin info:{}",notLoginException.getMessage());
//        return SaResult.error().setCode(401).setMsg(notLoginException.getMessage());
//    }
//        catch (Throwable e) {
//    e.printStackTrace();
//    log.error("验证重复提交时出现未知异常!");
//    return SaResult.error().setMsg("验证重复提交时出现未知异常").setCode(500);
//}
    }
}

package cn.duckflew.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalHandler
{

    @ExceptionHandler(value = JobRankIdInvalidException .class)
    public SaResult jobRankIdInvalidException(JobRankIdInvalidException jobRankIdInvalidException)
    {
        log.error("{}:{}",jobRankIdInvalidException.getMessage(),jobRankIdInvalidException.getErrorJobRankId());
        return SaResult.error().setCode(400).setMsg(jobRankIdInvalidException.getMessage()+":"+jobRankIdInvalidException.getErrorJobRankId());
    }
    @ExceptionHandler(value = PaidQuestionException .class)
    public SaResult paidQuestionException(PaidQuestionException paidQuestionException)
    {
        log.error("{}:{}",paidQuestionException.getMessage(),paidQuestionException.getErrorOrderId());
        return SaResult.error().setCode(500).setMsg(paidQuestionException.getMessage()+":"+paidQuestionException.getErrorOrderId());
    }
    @ExceptionHandler(value = OrderIdInvalidException .class)
    public SaResult orderIdInvalidException(OrderIdInvalidException orderIdInvalidException)
    {
        log.error("{}:{}",orderIdInvalidException.getMessage(),orderIdInvalidException.getErrorOrderId());
        return SaResult.error().setCode(400).setMsg(orderIdInvalidException.getMessage()+":"+orderIdInvalidException.getErrorOrderId());
    }
    @ExceptionHandler(value = CardIdExistException .class)
    public SaResult cardIdExistException(CardIdExistException cardIdExistException)
    {
        log.error("{}:{}",cardIdExistException.getMessage(),cardIdExistException.getCardId());
        return SaResult.error().setCode(400).setMsg(cardIdExistException.getMessage()+":"+cardIdExistException.getCardId());
    }

    @ExceptionHandler(value = EduDataSourceIdInvalidException .class)
    public SaResult eduDataSourceIdInvalidException(EduDataSourceIdInvalidException eduDataSourceIdInvalidException)
    {
        log.error("{}:{}",eduDataSourceIdInvalidException.getMessage(),eduDataSourceIdInvalidException.getErrorId());
        return SaResult.error().setCode(400).setMsg(eduDataSourceIdInvalidException.getMessage()+":"+eduDataSourceIdInvalidException.getErrorId());
    }
    @ExceptionHandler(value = BadFileFormatException .class)
    public SaResult badFileFormatException(BadFileFormatException badFileFormatException)
    {
        log.error("{}:错误文件格式={}",badFileFormatException.getMessage(),badFileFormatException.getBadFileSuffix());
        return SaResult.error().setCode(400).setMsg(badFileFormatException.getMessage()+"错误格式:"+badFileFormatException.getBadFileSuffix());
    }
    @ExceptionHandler(value = NotPreProfessorException .class)
    public SaResult notPreProfessorException(NotPreProfessorException notPreProfessorException)
    {
        log.error("{}:{}",notPreProfessorException.getMessage(),notPreProfessorException.getUserId());
        return SaResult.error().setCode(400).setMsg(notPreProfessorException.getMessage()+notPreProfessorException.getUserId());
    }
    @ExceptionHandler(value = ProInfoNotExistException .class)
    public SaResult proInfoNotExistException(ProInfoNotExistException proInfoNotExistException)
    {
        log.error("{}:userId={}",proInfoNotExistException.getMessage(),proInfoNotExistException.getUserId());
        return SaResult.error().setCode(400).setMsg(proInfoNotExistException.getMessage());
    }

    @ExceptionHandler(value = FileIdInvalidException .class)
    public SaResult fileIdInvalidException(FileIdInvalidException fileIdInvalidException)
    {
        log.error("{}:FileId={}",fileIdInvalidException.getMessage(),fileIdInvalidException.getErrorFileId());
        return SaResult.error().setCode(400).setMsg(fileIdInvalidException.getMessage());
    }
    @ExceptionHandler(value = FileUploadException.class)
    public SaResult avatarUploadException(FileUploadException avatarUploadException)
    {
        log.error("用户:{},头像上传失败,原因是:{}",avatarUploadException.getUserId(),avatarUploadException.getMessage());
        return SaResult.error().setCode(400).setMsg("头像上传失败,请联系管理员");
    }
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public SaResult httpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException)
    {
        log.error("http请求格式错误,{}",httpMessageNotReadableException.getMessage());
        return SaResult.error().setCode(400).setMsg("http报文格式错误,请检查入参类型是否对应或者检查请求体格式");
    }
    @ExceptionHandler(value = NotLoginException.class)
    public SaResult notLogin(NotLoginException notLoginException)
    {
        log.warn("notlogin info:{}",notLoginException.getMessage());
        return SaResult.error().setCode(401).setMsg(notLoginException.getMessage());
    }
    @ExceptionHandler(value = NotPermissionException.class)
    public SaResult permissionInsufficient(NotPermissionException permissionException)
    {
        log.warn("permission access denied info:{}",permissionException.getMessage());
        return SaResult.error().setCode(403).setMsg(permissionException.getMessage());
    }
    @ExceptionHandler(value = SaSessionSerializeException.class)
    public SaResult saSessionSerializeException(SaSessionSerializeException saSessionSerializeException)
    {
        log.error("saSessionSerializeException info:{}",saSessionSerializeException.getMessage());
        return SaResult.error().setCode(500).setMsg("用户Session解析失败,请联系管理员");
    }
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public SaResult missingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException)
    {
        log.error("{},接口参数缺失:{}",missingServletRequestParameterException.getMessage(),missingServletRequestParameterException.getParameterName());
        return SaResult.error().setCode(400).setMsg(missingServletRequestParameterException.getParameterName()+"参数缺失");
    }

    @ExceptionHandler(value = UsernameExistException.class)
    public SaResult usernameExistException(UsernameExistException usernameExistException)
    {
        log.error("missingServletRequestParameterException info:{}",usernameExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("此用户名已经被注册");
    }
    @ExceptionHandler(value = NotRegisterException.class)
    public SaResult notRegisterException(NotRegisterException notRegisterException)
    {
        log.error("notRegisterException info:{}",notRegisterException.getMessage());
        return SaResult.error().setCode(400).setMsg("还未注册,请先注册");
    }
    @ExceptionHandler(value = BadCreditException.class)
    public SaResult badCreditException(BadCreditException badCreditException)
    {
        log.error("密码错误 errorPassword={}",badCreditException.getErrorPwd());
        return SaResult.error().setCode(400).setMsg("凭据错误,登录失败");
    }
    @ExceptionHandler(value = CheckCodeExpiredException.class)
    public SaResult checkCodeExpiredException(CheckCodeExpiredException checkCodeExpiredException)
    {
        log.error("{},checkCode:{}",checkCodeExpiredException.getMessage(),checkCodeExpiredException.getCheckCode());
        return SaResult.error().setCode(400).setMsg(checkCodeExpiredException.getMessage());
    }

    @ExceptionHandler(value = CheckCodeInvalidException.class)
    public SaResult checkCodeInvalidException(CheckCodeInvalidException checkCodeInvalidException)
    {
        log.error("checkCodeInvalidException info:{}",checkCodeInvalidException.getMessage());
        return SaResult.error().setCode(400).setMsg("验证码无效!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SaResult validationExceptionHandle(MethodArgumentNotValidException ex) {
        log.error("JavaBean参数校验不通过:{}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> msgList = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return SaResult.error().setCode(400).setMsg(String.join(",", msgList));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public SaResult constraintViolationException(ConstraintViolationException constraintViolationException)
    {
        log.error("普通参数非JavaBean参数校验不通过{}",constraintViolationException.getMessage());
        Set<ConstraintViolation<?>> errors = constraintViolationException.getConstraintViolations();
        List<String> msgList = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return SaResult.error().setCode(400).setMsg(String.join(",", msgList));
    }


    @ExceptionHandler(CVNotExistException.class)
    public SaResult cvNotExistException(CVNotExistException cvNotExistException)
    {
        log.error("redis中的简历文件信息不存在:{}",cvNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("请先上传简历");
    }

    @ExceptionHandler(RepeatReqProException.class)
    public SaResult repeatReqProException(RepeatReqProException repeatReqProException)
    {
        log.error("重复申请教授:{}",repeatReqProException.getMessage());
        return SaResult.error().setCode(400).setMsg("正在申请,请不要重复申请");
    }
    @ExceptionHandler(AlreadyIsPro.class)
    public SaResult alreadyIsProException(AlreadyIsPro alreadyIsProException)
    {
        log.error("重复申请教授:{}",alreadyIsProException.getMessage());
        return SaResult.error().setCode(400).setMsg("您已经成为志愿教授,请不要重复申请");
    }
    @ExceptionHandler(QuestionIdInvalidException.class)
    public SaResult questionIdInvalidException(QuestionIdInvalidException questionIdInvalidException)
    {
        log.error("问题id无效:{},id={}",questionIdInvalidException.getMessage(),questionIdInvalidException.getQuestionId());
        return SaResult.error().setCode(400).setMsg("问题id无效");
    }

    @ExceptionHandler(RoleNameExistException.class)
    public SaResult roleNameExistException(RoleNameExistException roleNameExistException)
    {
        log.error("角色名已经存在:{},roleName={}",roleNameExistException.getMessage(),roleNameExistException.getRoleName());
        return SaResult.error().setCode(400).setMsg("角色名已经存在,roleName="+roleNameExistException.getRoleName());
    }
    @ExceptionHandler(AreaNameExistException.class)
    public SaResult areaNameExistException(AreaNameExistException areaNameExistException)
    {
        log.error("{},areaName={}",areaNameExistException.getMessage(),areaNameExistException.getBadAreaName());
        return SaResult.error().setCode(400).setMsg(areaNameExistException.getMessage()+"areaName="+areaNameExistException.getBadAreaName());
    }
    @ExceptionHandler(MenuIdInvalidException.class)
    public SaResult permissionNotExist(MenuIdInvalidException menuIdInvalidException)
    {
        log.error("{}:{}",menuIdInvalidException.getMessage(),menuIdInvalidException.getErrorId());
        return SaResult.error().setCode(400).setMsg(menuIdInvalidException.getMessage());
    }
    @ExceptionHandler(StudyGuideIdInvalidException.class)
    public SaResult guideIdInvalidException(StudyGuideIdInvalidException guideIdInvalidException)
    {
        log.error("{}:{}",guideIdInvalidException.getMessage(),guideIdInvalidException.getErrGuideId());
        return SaResult.error().setCode(400).setMsg(guideIdInvalidException.getMessage());
    }
    @ExceptionHandler(UserAccountBanedException.class)
    public SaResult userAccountBanedException(UserAccountBanedException userAccountBanedException)
    {
        log.error("{}:{}",userAccountBanedException.getMessage(),userAccountBanedException.getUserId());
        return SaResult.error().setCode(400).setMsg(userAccountBanedException.getMessage());
    }

    @ExceptionHandler(RoleNotExistException.class)
    public SaResult roleNotExistException(RoleNotExistException roleNotExistException)
    {
        log.error("角色不存在:{}",roleNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("角色不存在");
    }


    @ExceptionHandler(AdminNotExistException.class)
    public SaResult adminNotExistException(AdminNotExistException adminNotExistException)
    {
        log.error("管理员账户不存在:{}",adminNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("管理员账户不存在");
    }
    @ExceptionHandler(AnswerNotExistException.class)
    public SaResult answerNotExistException(AnswerNotExistException answerNotExistException)
    {
        log.error("回答不存在:{},answerId={}",answerNotExistException.getMessage(),answerNotExistException.getAnswerId());
        return SaResult.error().setCode(400).setMsg("此回答不存在");
    }
    @ExceptionHandler(QuestionStatusException.class)
    public SaResult questionStatusException(QuestionStatusException questionStatusException)
    {
        log.error("问题状态错误:{}",questionStatusException.getMessage());
        return SaResult.error().setCode(400).setMsg("设置问题状态码出错,检查状态码是否合法");
    }

    @ExceptionHandler(BaseUserNotExistException.class)
    public SaResult baseUserNotExistException(BaseUserNotExistException baseUserNotExistException)
    {
        log.error("用户不存在:{}",baseUserNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("此用户不存在");
    }

    @ExceptionHandler(PhoneNumberNotBindException.class)
    public SaResult phoneNumberNotBindException(PhoneNumberNotBindException phoneNumberNotBindException)
    {
        log.error("未绑定手机号,无法继续操作:{}",phoneNumberNotBindException.getMessage());
        return SaResult.error().setCode(400).setMsg("手机号未绑定，请先绑定手机号");
    }

    @ExceptionHandler(PwdSameAsBeforeException.class)
    public SaResult pwdSameAsBeforeException(PwdSameAsBeforeException pwdSameAsBeforeException)
    {
        log.error("新密码与老密码相同:{}",pwdSameAsBeforeException.getMessage());
        return SaResult.error().setCode(400).setMsg("新密码不能与老密码相同");
    }
    @ExceptionHandler(PhoneNumberNotRegisterException.class)
    public SaResult phoneNumberNotRegisterException(PhoneNumberNotRegisterException phoneNumberNotRegisterException)
    {
        log.error("手机号未注册:{}",phoneNumberNotRegisterException.getMessage());
        return SaResult.error().setCode(400).setMsg("手机号未注册");
    }

    @ExceptionHandler(NewsNotExistException.class)
    public SaResult newsNotExistException(NewsNotExistException newsNotExistException)
    {
        log.error("资讯不存在:{}",newsNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg("资讯不存在");
    }
    @ExceptionHandler(NewsIdInvalidException.class)
    public SaResult newsIdInvalidException(NewsIdInvalidException newsIdInvalidException)
    {
        log.error("资讯id无效:{}",newsIdInvalidException.getMessage());
        return SaResult.error().setCode(400).setMsg("资讯id无效");
    }
    @ExceptionHandler(NewsCoverFileSaveException.class)
    public SaResult newsCoverFileSaveException(NewsCoverFileSaveException newsCoverFileSaveException)
    {
        log.error("资讯封面存储异常:{}",newsCoverFileSaveException.getMessage());
        return SaResult.error().setCode(400).setMsg("资讯封面上传失败");
    }

    @ExceptionHandler(ChatMsgSendFailureException.class)
    public SaResult chatMsgSendFailureException(ChatMsgSendFailureException chatMsgSendFailureException)
    {
        log.error("私聊消息发送失败:{}",chatMsgSendFailureException.getMessage());
        return SaResult.error().setCode(400).setMsg("发送失败");
    }

    @ExceptionHandler(AnswerPubRepeatException.class)
    public SaResult answerPubRepeatException(AnswerPubRepeatException answerPubRepeatException)
    {
        log.error("已经发布过回答，不能问题下重复发布:{}",answerPubRepeatException.getMessage());
        return SaResult.error().setCode(400).setMsg("您在此问题下已经有过回答 请不要重复发布,可以在原回答上进行修改");
    }

    @ExceptionHandler(TelephoneExistException.class)
    public SaResult telephoneExistException(TelephoneExistException telephoneExistException)
    {
        log.error("{},telephone={}",telephoneExistException.getMessage(),telephoneExistException.getNumber());
        return SaResult.error().setCode(400).setMsg(telephoneExistException.getMessage());
    }
    @ExceptionHandler(RealInfoNotExistException.class)
    public SaResult realInfoNotExistException(RealInfoNotExistException realInfoNotExistException)
    {
        log.error("{}",realInfoNotExistException.getMessage());
        return SaResult.error().setCode(400).setMsg(realInfoNotExistException.getMessage());
    }

    @ExceptionHandler(NotProfessorException.class)
    public SaResult notProfessorException(NotProfessorException notProfessorException)
    {
        log.error("{},errorId={}",notProfessorException.getMessage(),notProfessorException.getErrProId());
        return SaResult.error().setCode(400).setMsg(notProfessorException.getMessage());
    }


    @ExceptionHandler(ProfessorIdInvalidException.class)
    public SaResult notProfessorException(ProfessorIdInvalidException professorIdInvalidException)
    {
        log.error("{},errorId={}",professorIdInvalidException.getMessage(),professorIdInvalidException.getProfessorId());
        return SaResult.error().setCode(400).setMsg(professorIdInvalidException.getMessage());
    }
    @ExceptionHandler(GuideNameExistException.class)
    public SaResult guideNameExistException(GuideNameExistException guideNameExistException)
    {
        log.error("{},guideName={}",guideNameExistException.getMessage(),guideNameExistException.getStudyGuideName());
        return SaResult.error().setCode(400).setMsg(guideNameExistException.getMessage());
    }

}

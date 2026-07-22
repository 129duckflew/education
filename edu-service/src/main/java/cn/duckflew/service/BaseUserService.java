package cn.duckflew.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.document.ProfessorInfoDoc;
import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.professor.Answer;
import cn.duckflew.entity.system.SystemMsg;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.entity.UserConsultArea;
import cn.duckflew.enums.SysMsgType;
import cn.duckflew.enums.UserOpType;
import cn.duckflew.enums.UserType;
import cn.duckflew.mapper.*;
import cn.duckflew.mapper.admin.ProInfoMapper;
import cn.duckflew.utils.CardIdUtil;
import cn.duckflew.utils.StringUtil;
import cn.duckflew.vo.*;
import cn.duckflew.enums.LoginType;
import cn.duckflew.exception.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class BaseUserService extends ServiceImpl<BaseUserMapper, BaseUser> {


    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    BaseUserMapper baseUserMapper;
    public String getOnlyUsername()
    {
        log.info("随机生成用户用开始");
        char [] source=new char[36];
        for (int i = 0; i < 26; i++)
        {
            source[i]=(char)('a'+i);
        }
        for (int i = 26; i < 36; i++)
        {
            source[i]=(char)('0'+i-26);
        }
        StringBuilder sb=new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 8; i++)
        {
            int randValue = rand.nextInt(36);
            sb.append(source[randValue]);
        }
        BaseUser exist = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("username",sb.toString()));
        if (exist!=null)return getOnlyUsername();
        return sb.toString();
    }
    public SaResult registerBaseUser(RegisterParamsMailCheck registerParamsMailCheck) {
        if (baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("username", registerParamsMailCheck.getUsername()))!=null)
            throw new UsernameExistException();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String checkCode = (String) ops.get(registerParamsMailCheck.getEmail());
        if (StringUtil.isNullOrEmpty(checkCode))throw new CheckCodeExpiredException("邮箱验证码过期",checkCode);
        if (!registerParamsMailCheck.getEmailCheckCode().equals(checkCode))
            throw new CheckCodeInvalidException("邮箱验证码错误",checkCode);
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(registerParamsMailCheck,baseUser);
        baseUser.setPassword(SaSecureUtil.md5(registerParamsMailCheck.getPassword()));
        baseUser.setUserType(UserType.GUEST.getCode());
        baseUser.setCreateTime(new Date());
        if (baseUser.getUsername()==null)
        {
            try
            {
                baseUser.setUsername(getOnlyUsername());
            }catch (Exception e)
            {
                e.printStackTrace();
                log.error("随机生成用户名失败，用户信息:{}",baseUser.toString());
            }
        }
        baseUserMapper.insert(baseUser);
        return SaResult.ok().setMsg("注册成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public SaResult homeLogin(LoginParams loginParams) {
        String telephoneNumber = loginParams.getTelephoneNumber();
        String email=loginParams.getEmail();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        if (loginParams.getLoginType() == LoginType.EMAIL_LOGIN.getCode())
        {
            BaseUser loginUser = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("email", loginParams.getEmail()));
            if (loginUser==null)throw new NotRegisterException();
            if (loginUser.getPassword()==null||!loginUser.getPassword().equals(SaSecureUtil.md5(loginParams.getPassword())))
            {
                throw new BadCreditException("密码错误",loginParams.getPassword());
            }
            if (loginUser.getEnabled()!=null&&loginUser.getEnabled().equals(Boolean.FALSE))
            {
                throw new UserAccountBanedException("此账号已经被禁用",loginUser.getId());
            }
            SaLoginModel loginModel = SaLoginModel.create();
            loginModel.setExtra("loginType",LoginType.EMAIL_LOGIN);
            loginModel.setDevice("PC");
            StpUtil.login(loginUser.getId(),loginModel );
            if (email!=null&&ops.get(email)!=null)
            redisTemplate.delete(email);
            log.warn("登录成功,删除验证码redis缓存");
            return SaResult.ok().setMsg("登录成功").setData(StpUtil.getTokenInfo());
        }
        else if (loginParams.getLoginType() == LoginType.TELEPHONE_LOGIN.getCode()) {
            BaseUser loginUser = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("telephone_number", telephoneNumber));
            if (loginUser==null)return SaResult.error().setCode(400).setMsg("手机号尚未注册");
            if (!loginUser.getPassword().equals(SaSecureUtil.md5(loginParams.getPassword())))
            return SaResult.error().setCode(400).setMsg("密码错误");
            SaLoginModel loginModel = SaLoginModel.create();
            loginModel.setExtra("loginType",LoginType.TELEPHONE_LOGIN);
            loginModel.setDevice("PC");
            StpUtil.login(loginUser.getId(),loginModel );
            return SaResult.ok().setData(StpUtil.getTokenInfo());
        }
        else if (loginParams.getLoginType() == LoginType.USERNAME_LOGIN.getCode())
        {
            String username = loginParams.getUsername();
            String password = loginParams.getPassword();
            BaseUser baseUser = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("username", username).eq("password", SaSecureUtil.md5(password)));
            if (baseUser!=null)
            {
                SaLoginModel loginModel = SaLoginModel.create();
                loginModel.setExtra("loginType",LoginType.USERNAME_LOGIN);
                loginModel.setDevice("PC");
                StpUtil.login(baseUser.getId(),loginModel);
            }
            return SaResult.ok().setMsg("登陆成功").setData(StpUtil.getTokenInfo());
        }
        else if (loginParams.getLoginType()==LoginType.TELEPHONE_CHECK_CODE_LOGIN.getCode())
        {
            BaseUser baseUser = baseUserMapper.selectOne(
                    new QueryWrapper<BaseUser>().eq(
                            "telephone_number", telephoneNumber)
            );
            if (baseUser==null)
                throw new PhoneNumberNotRegisterException();
            if (ops.get(telephoneNumber)==null)
                throw new CheckCodeExpiredException("手机验证码过期",loginParams.getCheckCode());
            if (loginParams.getCheckCode().equals(ops.get(telephoneNumber)))
            {
                SaLoginModel loginModel = SaLoginModel.create();
                loginModel.setExtra("loginType",LoginType.TELEPHONE_CHECK_CODE_LOGIN);
                loginModel.setDevice("PC");
                StpUtil.login(baseUser.getId(),loginModel);
                return SaResult.ok().setMsg("登录成功").setData(StpUtil.getTokenInfo());
            }
            else
            {
                log.error("手机验证码登录不通过 手机号={}",loginParams.getTelephoneNumber());
                throw new CheckCodeInvalidException("手机登录验证码错误",loginParams.getCheckCode());
            }
        }
        else return SaResult.error().setCode(400).setMsg("登录类型错误");
    }

    public SaResult registerBaseUserByTextMsg(RegisterParamsTextMsgCheck registerParamsTextMsgCheck)
    {
        if (baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("telephone_number", registerParamsTextMsgCheck.getPhoneNumber()))!=null)
           return SaResult.error().setCode(400).setMsg("此电话号码已经被注册");
        if (baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("username", registerParamsTextMsgCheck.getUsername()))!=null)
            return SaResult.error().setCode(400).setMsg("此用户名已经被注册");
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String checkCode = (String) ops.get(registerParamsTextMsgCheck.getPhoneNumber());
        if (StringUtil.isNullOrEmpty(checkCode))throw new CheckCodeExpiredException("手机验证码过期",checkCode);
        if (!registerParamsTextMsgCheck.getCheckCode().equals(checkCode))
            throw new CheckCodeInvalidException("手机验证码错误",checkCode);
        BaseUser baseUser = new BaseUser();
        baseUser.setUsername(registerParamsTextMsgCheck.getUsername());
        baseUser.setPassword(SaSecureUtil.md5(registerParamsTextMsgCheck.getPassword()));
        baseUser.setTelephoneNumber(registerParamsTextMsgCheck.getPhoneNumber());
        baseUser.setUserType(UserType.GUEST.getCode());
        baseUser.setCreateTime(new Date());
        baseUserMapper.insert(baseUser);
        return SaResult.ok().setMsg("注册成功! 请登录后尽快完善个人信息");
    }

    @Transactional(rollbackFor = Exception.class)
    public SaResult bindMail(Integer userId,BindMailParams bindMailParams)
    {
        BaseUser baseUser = baseUserMapper.selectById(userId);
        String curMail = baseUser.getEmail();
        if (StringUtils.isNotBlank(curMail)&&curMail.equals(bindMailParams.getEmail()))
            return SaResult.error().setCode(400).setMsg("请不要重复绑定");
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        if (bindMailParams.getCheckCode().equals(ops.get(bindMailParams.getEmail())))
        {
            baseUser.setEmail(bindMailParams.getEmail());
            baseUserMapper.updateById(baseUser);
            return SaResult.ok().setMsg("绑定成功");
        }
        return SaResult.error().setMsg("绑定失败,验证码无效");
    }

    public SaResult mailExist(String email)
    {
        BaseUser user = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("email", email));
        if (user==null)
            return SaResult.ok().setMsg("此邮箱暂未被绑定");
        return SaResult.error().setMsg("此邮箱已经被绑定").setCode(400);
    }


    @Autowired
    UserConsultAreaMapper userConsultAreaMapper;

    @Transactional(rollbackFor = Exception.class)
    public void addInterestArea(int userId, List<String> areaIds)
    {
        userConsultAreaMapper.delete(
                new QueryWrapper<UserConsultArea>()
                .eq("user_id",userId )
        );
        log.warn("删除用户所有喜好");
        for (String areaId : areaIds)
        {
            UserConsultArea userConsultArea = new UserConsultArea();
            userConsultArea.setUserId(userId);
            userConsultArea.setAreaId(areaId);
            userConsultAreaMapper.insert(userConsultArea);
        }
        log.warn("重新添加用户喜好");
    }


    public void  setPhone(Integer userId, String phoneNumber,String checkCode)
    {
        BaseUser check = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("telephone_number", phoneNumber));
        if (check!=null)throw new TelephoneExistException("手机号已被绑定",phoneNumber );
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String code = (String) ops.get(phoneNumber);
        if (StringUtil.isNullOrEmpty(code))
            throw new CheckCodeExpiredException("手机验证码过期",checkCode);
        if (!code.equals(phoneNumber))
            throw new CheckCodeInvalidException("手机验证码错误",checkCode);
        BaseUser baseUser = new BaseUser();
        baseUser.setId(userId);
        baseUser.setTelephoneNumber(phoneNumber);
        baseUserMapper.updateById(baseUser);
    }

    @Autowired
    ProInfoMapper proInfoMapper;

    @Autowired
    SystemMsgMapper systemMsgMapper;
    @Autowired
    AnswerMapper answerMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Transactional(rollbackFor = Exception.class)
    public SaResult likeAnswer(Integer answerId, Integer userId,Integer likeStatus)
    {
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        HashOperations<String,String,Object> opsForHash = redisTemplate.opsForHash();
        opsForSet.add("answer:be_liked_list",answerId);
        log.debug("answerId={}添加到被点赞列表当中",answerId);
        opsForHash.put("answer:like:"+answerId,String.valueOf(userId),likeStatus);
        log.debug("点赞信息放入answerId={}的hash当中",answerId);
        Answer answer = answerMapper.selectById(answerId);
        log.debug("answerId={},answer={}",answerId,answer);
        if (answer==null)throw new AnswerNotExistException("给回答点赞时出现回答id无效",answerId);

        SystemMsg systemMsg = new SystemMsg();
        systemMsg.setCreateTime(new Date());
        Integer toUserId = answer.getProfessorId();
        systemMsg.setMsgType(SysMsgType.LIKE_ANSWER.getCode());
        systemMsg.setIsRead(0);
        systemMsg.setToUserId(toUserId);
        systemMsg.setFromUserId(0);
        systemMsg.setResourceId(answerId);
        systemMsg.setRelatedUserId(userId);
        systemMsgMapper.insert(systemMsg);
        log.debug("生成系统点赞消息:{}",systemMsg);
        return SaResult.ok().setMsg("点赞成功");
    }

    @Autowired
    UserOpService userOpService;
    @Transactional(rollbackFor = Exception.class)
    public SaResult collectAnswer(Integer answerId, Integer userId,Integer status)
    {
        userOpService.addOp(UserOpType.COLLECT_ANSWER,userId,answerId,status);
        return SaResult.ok().setMsg("操作成功");
    }

    public boolean isPro(int userId)
    {
        return baseUserMapper.selectById(userId).getUserType()== UserType.PROFESSOR.getCode();
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestToBeProfessor(Integer userId )
    {
        BaseUser user = baseUserMapper.selectById(userId);
        ProInfo proInfo = proInfoMapper.selectById(userId);
        if (proInfo==null)
        {
            proInfo=new ProInfo();
            proInfo.setId(userId);
            proInfoMapper.insert(proInfo);
        }
        String FileId = proInfo.getCvFile();
        if (StringUtil.isNullOrEmpty(FileId))
            throw new CVNotExistException();
        if (user.getUserType()==UserType.PRE_PROFESSOR.getCode())
           throw new RepeatReqProException();
        if (user.getUserType()==UserType.PROFESSOR.getCode())
            throw new AlreadyIsPro();
        if (user.getRealName()==null||user.getCardId()==null)
            throw new RealInfoNotExistException("真名或身份证号未填写");
        user.setUserType(UserType.PRE_PROFESSOR.getCode());
        updateById(user);
    }

    public UserInfo profile(int userId)
    {
        UserInfo res = new UserInfo();
        BaseUser basicInfo = getById(userId);
        basicInfo.setPassword(null);
        basicInfo.setCardId(StringUtil.encodeCardId(basicInfo.getCardId()));
        ProInfo proInfo = proInfoMapper.selectById(userId);
        res.setBasicInfo(basicInfo);
        res.setProInfo(proInfo);
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(int userId, String checkCode, String password)
    {
        BaseUser baseUser = baseUserMapper.selectById(userId);
        if (baseUser==null) throw new BaseUserNotExistException();
        if (baseUser.getTelephoneNumber()==null)throw new PhoneNumberNotBindException();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String code = (String) ops.get(baseUser.getTelephoneNumber());
        if (StringUtil.isNullOrEmpty(code))
        {
            log.error("重置密码 手机号验证不通过");
            throw new CheckCodeExpiredException("手机验证码过期",checkCode);
        }
        if (!code.equals(checkCode))
        {
            log.error("重置密码 手机号验证不通过");
            throw new CheckCodeInvalidException("手机验证码无效",checkCode);
        }
        if (baseUser.getPassword().equals(SaSecureUtil.md5(password)))
        {
            throw new PwdSameAsBeforeException();
        }
        baseUser.setPassword(SaSecureUtil.md5(password));
        baseUserMapper.updateById(baseUser);
    }


    @Autowired
    ConsultAreaService consultAreaService;
    @Autowired
    ProfessorService professorService;
    public Map<String, Object> getProfileById(int loginUserId, Integer userId) {
        Map<String, Object> res = new HashMap<>();
        UserInfo userInfo = this.profile(userId);
        if (this.isPro(userId)) {
            List<ConsultArea> answerAreaList = professorService.getAnswerAreaList(userId);
            res.put("answerAreaList", answerAreaList);
        }
        if (loginUserId != userId)
        {
            BaseUser basicInfo = userInfo.getBasicInfo();
            basicInfo.setTelephoneNumber(null);
            basicInfo.setCardId(null);
            basicInfo.setRealName(null);
        }
        res.put("userInfo", userInfo);
        List<ConsultArea> likeAreaList=consultAreaService.userLikeAreaList(userId);
        res.put("likeAreaList",likeAreaList);
        return res;
    }


    public void setUserGender( String gender, int userId)
    {
        BaseUser user = baseMapper.selectById(userId);
        user.setGender(gender);
        baseUserMapper.updateById(user);
    }

    @Autowired
    EsService esService;
    public void setRealName(int userId, String realName)
    {
        if (isPro(userId)){
            ProfessorInfoDoc doc = professorService.getProfessorDoc(baseUserMapper.selectById(userId));
            esService.saveProfessorInfoDoc(doc);
        }
        BaseUser user = baseUserMapper.selectById(userId);
        user.setRealName(realName);
        baseUserMapper.updateById(user);
    }

    public void setCardId(int userId, String cardId)
    {
        BaseUser user = baseUserMapper.selectById(userId);
        user.setCardId(cardId);
        user.setGender(CardIdUtil.getSex(cardId));
        try
        {
            user.setBirthday(CardIdUtil.getBirthday(cardId));
        } catch (ParseException e)
        {
            e.printStackTrace();
            log.error("解析身份证异常,身份证号:{}",cardId);
        }
        baseUserMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void banBaseUser(  List<Integer> ids)
    {
        ids.forEach(id->{
            BaseUser baseUser = baseUserMapper.selectById(id);
            if (baseUser==null) throw new BaseUserNotExistException();
            baseUser.setEnabled(false);
            baseUserMapper.updateById(baseUser);
        });
    }

    public void checkUsernameRepeat(String checkUsername,boolean isUpdate,Integer checkId)
    {
        if (baseUserMapper.selectById(checkId)==null)throw new BaseUserNotExistException();
        BaseUser checkUser = baseUserMapper.selectOne(new QueryWrapper<BaseUser>().eq("username", checkUsername));
        if (checkUser!=null)
        {
            if (isUpdate&&!checkUser.getId().equals(checkId))
                throw new UsernameExistException();
        }
    }

    public void updateUser(BaseUser baseUser)
    {
        if (baseUserMapper.selectById(baseUser.getId())==null)
                throw new BaseUserNotExistException();
        if (baseUser.getUsername()!=null)
            checkUsernameRepeat(baseUser.getUsername(),true,baseUser.getId());
        baseUserMapper.updateById(baseUser);
    }

}

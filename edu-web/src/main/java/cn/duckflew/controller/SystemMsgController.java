package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.entity.system.ChatWindow;
import cn.duckflew.entity.system.SystemMsg;
import cn.duckflew.service.ChatWindowService;
import cn.duckflew.service.SystemMsgService;
import cn.duckflew.vo.ChatInfo;
import cn.duckflew.vo.ChatParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统消息接口
 * @apiNote 默认采用轮训策略 可以设置60s请求一次看是否有自己的消息
 */
@RequestMapping("/msg")
@RestController
@Validated
public class SystemMsgController
{

    @Autowired
    SystemMsgService msgService;
    /**
     * 获取自己的所有未读消息总数
     * @return
     * @response {
     *           "code": 200,
     *           "msg": "ok",
     *           "data": 0 //未读的消息总数
     *         }
     */
    @SaCheckLogin
    @GetMapping("/not_read_count")
    public SaResult getAllMsgToMe()
    {
        int userId = StpUtil.getLoginIdAsInt();
        Map<String, Object>res=systemMsgService.allTypeNotReadCount(userId);
        return SaResult.ok().setData(res);
    }

    /**
     * 根据消息类型获取类别下的所有消息
     * @param msgType 消息类型
     * @apiNote  0:系统广播消息 1:私聊 2:回答被点赞 3;回答被收藏 4:收到回答 5:收到问题邀请
     * 6:问题被点赞 7:问题被封禁 8:回答被封禁 9:问题审核通过 10:问题审核不通过 11:教授收到评价
     * 12: 回答审核通过 13:回答审核不通过
     * @response
     * {
     *     [ "test": 1]
     * }
     */
    @SaCheckLogin
    @GetMapping("/type/{msgType}")
    public SaResult getMsgByType(
//                @Pattern (regexp = "$[0123]*^",message = "消息类型错误")
                @Max(value = 12,message = "消息类型错误")
                @PathVariable
                @NotNull(message = "消息类型不能为空")
                 Integer msgType)
    {
        List<Map<String,Object>> list=null;
        int userId = StpUtil.getLoginIdAsInt();
        list=msgService.list(new QueryWrapper<SystemMsg>()
                .eq("msg_type", msgType)
                .eq("to_user_id",userId)
                .orderByDesc("create_time")
        ).stream().map(msgService::msgLoadData).collect(Collectors.toList());
        return SaResult.ok().setData(list);
    }


    /**
     * 根据id获取消息详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    public SaResult getSysMsgById(@NotNull(message = "id不能为空") @PathVariable Integer id)
    {
        int userId = StpUtil.getLoginIdAsInt();
        return SaResult.ok().setData(msgService.msgLoadData(msgService.getOne(
                new QueryWrapper<SystemMsg>()
                        .eq("to_user_id",userId)
                        .eq("id",id))
        ));
    }


    @Autowired
    SystemMsgService systemMsgService;

    /**
     * 设置msgId消息已读
     * @param msgId 消息id
     * @return
     */
    @PostMapping("/is_read/{msgId}")
    @SaCheckLogin
    public SaResult setIsRead(@NotNull(message = "消息id不能为空")@PathVariable Integer msgId)
    {
        boolean opStatus=systemMsgService.setIsRead(msgId,StpUtil.getLoginIdAsInt());
        if (opStatus)return SaResult.ok().setMsg("设置已读成功");
        return SaResult.error().setCode(400).setMsg("设置已读失败");
    }

    /**
     * 向某人发送私聊消息
     * @param chatParam 聊天参数
     * @return
     */
    @PostMapping("/chat")
    @SaCheckLogin
    public SaResult chatToSomeOne(@Validated @RequestBody ChatParam chatParam)
    {
        Integer fromUserId = StpUtil.getLoginIdAsInt();
        systemMsgService.chat(fromUserId,chatParam.getMsgContent(),chatParam.getUserId());
        return SaResult.ok().setMsg("发送成功");
    }

    /**
     * 获取私聊用户列表
     * @return
     */

    @GetMapping("/chat/list")
    @SaCheckLogin
    public SaResult getChatList()
    {
        int userId = StpUtil.getLoginIdAsInt();
        List<ChatInfo> chatInfoList= systemMsgService.getChatList(userId);
        return SaResult.ok().setData(chatInfoList);
    }

    /**
     * 获取和某人的聊天记录
     * @param fromUserId 聊天对象
     * @return
     */
    @GetMapping("/chat/record/{fromUserId}")
    @SaCheckLogin
    public SaResult getChatRecordList(@PathVariable Integer fromUserId)
    {
        int userId = StpUtil.getLoginIdAsInt();
        List<Map<String,Object>> res=systemMsgService.getMsgRecordList(fromUserId,userId);
        return SaResult.ok().setData(res);
    }

    @Autowired
    ChatWindowService chatWindowService;

    /**
     * 打开与某个对象的聊天窗口
     * @param toUserId 聊天的用户id
     * @return
     */
    @PostMapping("/chat/open/{toUserId}")
    @SaCheckLogin
    public SaResult openChat(
            @NotNull(message = "用户id不能为空")
            @PathVariable Integer toUserId
    )
    {
        Integer fromUserId = StpUtil.getLoginIdAsInt();
        ChatWindow chatWindow = new ChatWindow();
        chatWindow.setFromUserId(fromUserId);
        chatWindow.setToUserId(toUserId);
        chatWindowService.addWindow(fromUserId,toUserId);
        return SaResult.ok().setMsg("添加聊天对象成功");
    }

}

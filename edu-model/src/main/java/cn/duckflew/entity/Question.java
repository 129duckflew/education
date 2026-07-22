package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("question")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Question
{
    /**
     * 问题id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 问题标题
     */
    private String questionTitle;
    /**
     * 问题描述
     */
    private String questionDesc;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 问题状态
     * @since 0-> 禁止查看  1->正常展示 2->待审核,3->审核不通过 4->用户不公开
     */
    private Integer questionStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 订单号(免费问答则为空)
     */
    private String orderId;

}

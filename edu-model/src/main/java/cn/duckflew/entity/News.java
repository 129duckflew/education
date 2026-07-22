package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 系统资讯
 */
@TableName("t_news")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class News
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String newsTitle;
    private Date createTime;
    private Integer createBy;
    private String newsContent;
    private Integer priority;
    private String cover;
    private Integer indexShow;
}

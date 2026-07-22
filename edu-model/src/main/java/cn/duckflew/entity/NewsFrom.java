package cn.duckflew.entity;

import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("news_from")
public class NewsFrom
{
    @TableId(type = IdType.AUTO)
    @Null(groups = {AddGroup.class, UpdateGroup.class},message = "不允许指定来源id")
    private Integer id;
    /**
     * 资讯id
     */
    @Null(groups = {AddGroup.class, UpdateGroup.class},message = "更新不允许在fromList中指定newsId")
    private Integer newsId;
    /**
     * 来源标题
     */
    @NotBlank(message = "来源标题不能为空",groups = {AddGroup.class})
    private String sourceTitle;
    /**
     * 来源url
     */
    @URL(message = "url格式不正确",groups = {AddGroup.class})
    @NotBlank(message = "来源url不能为空",groups = {AddGroup.class})
    private String sourceUrl;
}

package cn.duckflew.entity.professor;

import cn.duckflew.validate.group.AddGroup;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import java.util.Date;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("edu_datasource")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EduDataSource
{
    /**
     * 学信网记录ID
     */
    @Null(message = "添加不允许指定id",groups = AddGroup.class)
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 身份证号
     */
    @NotNull(message = "添加必须指定身份证",groups = AddGroup.class)
    private String cardId;
    /**
     * 真名
     */
    @NotNull(message = "必须指定真名",groups = AddGroup.class)
    private String realName;
    /**
     * 生日
     */
    @NotNull(message = "必须指定生日",groups = AddGroup.class)
    @Past(message = "生日必须是一个过去的时间",groups = AddGroup.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 籍贯所在地
     */
    @NotNull(message = "必须指定籍贯所在地",groups = AddGroup.class)
    private String birthPlace;
    /**
     * 是否全日制
     */
    @NotNull(message = "必须指定是否全日制",groups = AddGroup.class)
    private Boolean isAllDay;
}

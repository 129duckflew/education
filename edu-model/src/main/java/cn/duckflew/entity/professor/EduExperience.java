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
@TableName("edu_experience")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EduExperience
{
    /**
     * id
     */
    @Null(message = "添加不允许指定id",groups = AddGroup.class)
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 学信网记录id
     */
    @Null(message = "添加不允许指定id",groups = AddGroup.class)
    private Integer eduDatasourceId;
    /**
     * 本次教育经历开始时间
     */
    @Past
    @NotNull(message = "必须指定开始时间",groups = AddGroup.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    /**
     * 本次教育结束开始时间
     */
    @NotNull(message = "必须指定结束时间",groups = AddGroup.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /**
     * 学校Id
     */
    @NotNull(message = "必须指定学校id",groups = AddGroup.class)
    private String schoolId;
    /**
     * 学位
     */
    @NotNull(message = "必须指定学位id",groups = AddGroup.class)
    private Integer degreeId;
    /**
     * 专业Id
     */
    @NotNull(message = "必须指定专业id",groups = AddGroup.class)
    private Integer majorId;
    /**
     * 是否全日制
     */
    @NotNull(message = "必须指定是否全日制",groups = AddGroup.class)
    private Boolean isAllDay;

    /**
     * 研究方向id 如果是研究生就需要指定
     */
    private String researchDirection;
}

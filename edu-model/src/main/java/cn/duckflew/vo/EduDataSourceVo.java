package cn.duckflew.vo;

import cn.duckflew.entity.professor.EduDataSource;
import cn.duckflew.validate.group.AddGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class EduDataSourceVo extends EduDataSource
{
    @NotEmpty(message = "教育经历不能为空",groups = AddGroup.class)
    @Valid
    @Getter
    @Setter
    private List<EduExperienceVo> eduExperienceList;
}

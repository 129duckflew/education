package cn.duckflew.vo;

import cn.duckflew.entity.ResearchDirection;
import cn.duckflew.entity.University;
import cn.duckflew.entity.UniversityMajor;
import cn.duckflew.entity.professor.Degree;
import cn.duckflew.entity.professor.EduExperience;
import cn.duckflew.validate.group.AddGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

public class EduExperienceVo extends EduExperience
{
    /**
     * 大学
     */
    @Setter
    @Getter
    @Null(message = "添加时无需指定此字段" ,groups = AddGroup.class)
    private University university;
    /**
     * 学位
     */
    @Setter
    @Getter
    @Null(message = "添加时无需指定此字段" ,groups = AddGroup.class)
    private Degree degree;
    /**
     * 专业
     */
    @Setter
    @Getter
    @Null(message = "添加时无需指定此字段" ,groups = AddGroup.class)
    private UniversityMajor major;

}

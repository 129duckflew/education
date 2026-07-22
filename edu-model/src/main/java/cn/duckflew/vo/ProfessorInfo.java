package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import cn.duckflew.entity.ProInfo;
import cn.duckflew.entity.professor.JobRank;
import cn.duckflew.entity.user.BaseUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public class ProfessorInfo  extends BaseUser
{

    /**
     * 职称相关
     */
    @Setter
    @Getter
    private JobRank jobRank;


    /**
     * 教授信息
     */
    @Setter
    @Getter
    private ProInfo proInfo;

    /**
     * 回答领域
     */
    @Getter
    @Setter
    private List<ConsultArea> answerAreaList;
}

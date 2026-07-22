package cn.duckflew.vo;

import cn.duckflew.entity.ConsultArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProIntroduction
{
    /**
     * id
     */
    private Integer professorId;
    /**
     * 真名
     */
    private String realName;
    /**
     * 职称名
     */
    private String jobRankName;
    /**
     *    简介
     */
    private String introduction;

    /**
     *  特点，逗号分隔
     */
    private List<ConsultArea> answerAreaList;
    /**
     * 头像
     */
    private String avatar;

}

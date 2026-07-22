package cn.duckflew.vo;

import cn.duckflew.entity.StudyGuide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGuideVo
{
    /**
     * id
     */
    private String id;
    /**
     * 父节点id
     */
    private String parentId;
    /**
     * 节点名
     */
    private String nodeName;
    /**
     * 子节点数组
     */
    List<StudyGuideVo> children;



    public StudyGuideVo(StudyGuide studyGuide)
    {
        this.id=studyGuide.getId();
        this.parentId=studyGuide.getParentId();
        this.nodeName=studyGuide.getNodeName();
    }
}

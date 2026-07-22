package cn.duckflew.vo;

import cn.duckflew.entity.StudyGuide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendStudyGuidePage
{
    private Integer total;
    private List<StudyGuide> studyGuides;
}

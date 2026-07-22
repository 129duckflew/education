package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("study_guide_consult_area")
public class StudyGuideConsultArea
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String consultAreaId;
    private String guideRootId;
}

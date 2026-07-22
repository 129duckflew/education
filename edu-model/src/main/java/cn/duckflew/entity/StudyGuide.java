package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGuide
{
    @TableId
    private String id;
    private String nodeName;
    private Integer isImportant;
    private String parentId;
}

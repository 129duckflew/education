package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("university_major")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UniversityMajor
{
    @TableId
    private Integer id;
    private String code;
    private String name;
    private Integer parentId;
}

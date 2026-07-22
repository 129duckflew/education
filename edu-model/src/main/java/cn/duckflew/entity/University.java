package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class University
{
    /**
     * 学校id
     */
    @TableId
    private String schoolId;
    /**
     * 学校名
     */
    private String schoolName;
    private Integer provinceId;
    private String provinceName;
    private Integer cityId;
    /**
     * 城市名
     */
    private String cityName;
    /**
     * 办学等级
     */
    private String level;
    private String other;
    /**
     * 所属部门
     */
    private String department;

}

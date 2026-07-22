package cn.duckflew.entity.professor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 职称对象
 */
@SuppressWarnings("AlibabaClassMustHaveAuthor")
@TableName("t_job_rank")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRank {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 职称名
     */
    private String jobRankName;
}

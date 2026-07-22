package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dictionary
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String dicName;
    private String dicValue;
    /**
     * 1 int
     * 2 string
     */
    private Integer dicValueType;
}

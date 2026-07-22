package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@TableName("t_pro_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProInfo
{
    /**
     * id
     */
    @Id
    private Integer id ;
    /**
     * 职称id
     */
    private Integer jobRankId;
    /**
     *  简历文件名
     */
    private String cvFile;
    /**
     * 教授简介
     */
    private String introduction;

    /**
     * 单次付费问答价格
     */
    private BigDecimal consultPrice;

}

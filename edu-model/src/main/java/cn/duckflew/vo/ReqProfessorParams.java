package cn.duckflew.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author duckflew
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqProfessorParams
{
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空!")
    String realName;
    /**
     * 出生日期 格式 yyyy-MM-dd
     */
    @NotNull(message = "生日不能为空")
    @Past(message = "生日必须是过去的时间!")
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date birthday;
    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{15}|\\d{18}$",message = "身份证格式错误")
    String cardId;
}

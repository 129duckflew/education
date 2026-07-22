package cn.duckflew.vo.admin;

import cn.duckflew.entity.NewsFrom;
import cn.duckflew.validate.group.AddGroup;
import cn.duckflew.validate.group.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsParam
{
    @Null(groups = AddGroup.class,message = "添加时必须为空")
    @NotNull(groups = UpdateGroup.class,message = "更新时必须指定newsId")
    private Integer newsId;
    /**
     * 资讯标题
     */
    @NotBlank(message = "标题不能为空",groups = AddGroup.class)
    private String newsTitle;
    /**
     * 资讯正文
     */
    @NotBlank(message = "正文不能为空",groups = AddGroup.class)
    private String newsContent;
    /**
     * 首页置顶优先级 数字越小排在越上面
     */
    @NotNull(message = "优先级不能为空",groups = AddGroup.class)
    @Min(value = 0 ,message = "最小值为0",groups = AddGroup.class)
    private Integer priority;
    /**
     * 封面文件的地址(只要文件名)
     */
    @NotBlank(message = "封面不能为空",groups = AddGroup.class)
    private String cover;
    /**
     * 是否首页展示 0=false,1=true
     */
    @NotNull(message = "是否首页展示不能为空",groups = AddGroup.class)
    @Min(value =0,message = "只能为0/1",groups = AddGroup.class)
    @Max(value =1,message = "只能为0/1",groups = AddGroup.class)
    private Integer indexShow;

    /**
     *  资讯来源数组
     */
    @NotEmpty(message = "资讯来源不能为空",groups = {AddGroup.class})
    @Valid
    private List<NewsFrom> newsFromList;
}

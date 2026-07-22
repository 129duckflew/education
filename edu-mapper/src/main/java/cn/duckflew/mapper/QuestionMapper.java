package cn.duckflew.mapper;

import cn.duckflew.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question>
{
    Page<Integer> pageQuestionIdByAreaId(Page<Integer> page,
                                         @RequestParam("likeAreaIdList") List<String> likeAreaIdList);
}

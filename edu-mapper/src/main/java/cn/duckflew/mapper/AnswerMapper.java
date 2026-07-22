package cn.duckflew.mapper;


import cn.duckflew.entity.professor.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface AnswerMapper extends BaseMapper<Answer>
{
    Integer getQuesTopAnswerId(@Param("questionId") int questionId);
}

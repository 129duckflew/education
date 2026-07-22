package cn.duckflew.mapper;

import cn.duckflew.entity.user.UserOp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserOpMapper extends BaseMapper<UserOp>
{
}

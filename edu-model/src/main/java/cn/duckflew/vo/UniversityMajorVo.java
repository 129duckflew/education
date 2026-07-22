package cn.duckflew.vo;

import cn.duckflew.entity.UniversityMajor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UniversityMajorVo extends UniversityMajor
{
    @Setter
    @Getter
    List<UniversityMajorVo> children;
}

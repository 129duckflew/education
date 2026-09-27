package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.professor.Degree;
import cn.duckflew.education.professor.DegreeRepository;
import cn.duckflew.education.professor.JobRank;
import cn.duckflew.education.professor.JobRankRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共字典数据（职称、学位等），供前端下拉选择。
 */
@RestController
@RequestMapping("/api/public/meta")
public class MetaController {

    private final JobRankRepository jobRankRepository;
    private final DegreeRepository degreeRepository;

    public MetaController(JobRankRepository jobRankRepository, DegreeRepository degreeRepository) {
        this.jobRankRepository = jobRankRepository;
        this.degreeRepository = degreeRepository;
    }

    @GetMapping("/job-ranks")
    public ApiResponse<List<JobRank>> jobRanks() {
        return ApiResponse.ok(jobRankRepository.findAll());
    }

    @GetMapping("/degrees")
    public ApiResponse<List<Degree>> degrees() {
        return ApiResponse.ok(degreeRepository.findAll());
    }
}

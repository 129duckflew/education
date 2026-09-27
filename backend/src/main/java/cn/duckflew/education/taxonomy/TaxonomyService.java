package cn.duckflew.education.taxonomy;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaxonomyService {

    private final UniversityRepository universityRepository;
    private final UniversityMajorRepository majorRepository;
    private final ResearchDirectionRepository directionRepository;

    public TaxonomyService(UniversityRepository universityRepository,
                           UniversityMajorRepository majorRepository,
                           ResearchDirectionRepository directionRepository) {
        this.universityRepository = universityRepository;
        this.majorRepository = majorRepository;
        this.directionRepository = directionRepository;
    }

    // ---------- 学校 ----------

    @Transactional(readOnly = true)
    public Page<University> searchUniversities(String name, Pageable pageable) {
        String keyword = (name == null || name.isBlank()) ? "" : name.trim();
        return universityRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Transactional
    public University saveUniversity(Long id, TaxonomyRequests.UniversityRequest request) {
        University entity = id == null ? new University() : universityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "学校不存在"));
        entity.setName(request.name());
        entity.setProvince(request.province());
        entity.setCity(request.city());
        entity.setLevel(request.level());
        entity.setDepartment(request.department());
        return universityRepository.save(entity);
    }

    @Transactional
    public void deleteUniversity(Long id) {
        universityRepository.deleteById(id);
    }

    // ---------- 专业 ----------

    @Transactional(readOnly = true)
    public List<UniversityMajor> listMajors(Long parentId) {
        return parentId == null ? majorRepository.findAll() : majorRepository.findByParentId(parentId);
    }

    @Transactional
    public UniversityMajor saveMajor(Long id, TaxonomyRequests.MajorRequest request) {
        UniversityMajor entity = id == null ? new UniversityMajor() : majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "专业不存在"));
        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setParentId(request.parentId());
        return majorRepository.save(entity);
    }

    @Transactional
    public void deleteMajor(Long id) {
        majorRepository.deleteById(id);
    }

    // ---------- 研究方向 ----------

    @Transactional(readOnly = true)
    public List<ResearchDirection> listDirections(Long majorId) {
        return majorId == null ? directionRepository.findAll() : directionRepository.findByMajorId(majorId);
    }

    @Transactional
    public ResearchDirection saveDirection(Long id, TaxonomyRequests.ResearchDirectionRequest request) {
        ResearchDirection entity = id == null ? new ResearchDirection() : directionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "研究方向不存在"));
        entity.setName(request.name());
        entity.setMajorId(request.majorId());
        return directionRepository.save(entity);
    }

    @Transactional
    public void deleteDirection(Long id) {
        directionRepository.deleteById(id);
    }
}

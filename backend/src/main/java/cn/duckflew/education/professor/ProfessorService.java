package cn.duckflew.education.professor;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.messaging.NotificationService;
import cn.duckflew.education.messaging.NotificationType;
import cn.duckflew.education.taxonomy.ConsultArea;
import cn.duckflew.education.taxonomy.ConsultAreaRepository;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import cn.duckflew.education.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    private final ProfessorProfileRepository profileRepository;
    private final ProfessorAreaRepository areaRepository;
    private final ProfessorReviewRepository reviewRepository;
    private final EducationRecordRepository educationRepository;
    private final JobRankRepository jobRankRepository;
    private final DegreeRepository degreeRepository;
    private final ConsultAreaRepository consultAreaRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProfessorService(ProfessorProfileRepository profileRepository,
                            ProfessorAreaRepository areaRepository,
                            ProfessorReviewRepository reviewRepository,
                            EducationRecordRepository educationRepository,
                            JobRankRepository jobRankRepository,
                            DegreeRepository degreeRepository,
                            ConsultAreaRepository consultAreaRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.profileRepository = profileRepository;
        this.areaRepository = areaRepository;
        this.reviewRepository = reviewRepository;
        this.educationRepository = educationRepository;
        this.jobRankRepository = jobRankRepository;
        this.degreeRepository = degreeRepository;
        this.consultAreaRepository = consultAreaRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // ---------- 申请 / 审核 ----------

    @Transactional
    public ProfessorProfile apply(Long userId, ProfessorRequests.ApplyRequest request) {
        User user = requireUser(userId);
        if (user.getRole() == UserRole.PROFESSOR) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已经是教授");
        }
        ProfessorProfile profile = profileRepository.findById(userId).orElseGet(ProfessorProfile::new);
        profile.setUserId(userId);
        applyFields(profile, request.jobRankId(), request.introduction(), request.consultPrice(), request.cvFileId());
        profile.setApproved(false);
        return profileRepository.save(profile);
    }

    @Transactional
    public void approve(Long userId) {
        ProfessorProfile profile = requireProfile(userId);
        profile.setApproved(true);
        profileRepository.save(profile);
        User user = requireUser(userId);
        user.setRole(UserRole.PROFESSOR);
        userRepository.save(user);
        notificationService.notify(userId, null, NotificationType.PROFESSOR_APPROVED, userId, null);
    }

    @Transactional
    public void reject(Long userId) {
        ProfessorProfile profile = requireProfile(userId);
        profile.setApproved(false);
        profileRepository.save(profile);
        User user = requireUser(userId);
        if (user.getRole() == UserRole.PROFESSOR) {
            user.setRole(UserRole.USER);
            userRepository.save(user);
        }
        notificationService.notify(userId, null, NotificationType.PROFESSOR_REJECTED, userId, null);
    }

    @Transactional
    public ProfessorProfile updateMe(Long userId, ProfessorRequests.UpdateRequest request) {
        ProfessorProfile profile = requireApprovedProfile(userId);
        applyFields(profile, request.jobRankId(), request.introduction(), request.consultPrice(), request.cvFileId());
        return profileRepository.save(profile);
    }

    @Transactional
    public void setAreas(Long userId, List<Long> areaIds) {
        requireApprovedProfile(userId);
        areaIds.forEach(areaId -> {
            if (!consultAreaRepository.existsById(areaId)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "领域不存在: " + areaId);
            }
        });
        areaRepository.deleteByProfessorId(userId);
        List<ProfessorArea> entities = areaIds.stream().distinct().map(areaId -> {
            ProfessorArea entity = new ProfessorArea();
            entity.setProfessorId(userId);
            entity.setAreaId(areaId);
            return entity;
        }).toList();
        areaRepository.saveAll(entities);
    }

    @Transactional
    public void replaceEducations(Long userId, List<ProfessorRequests.EducationRequest> requests) {
        requireApprovedProfile(userId);
        educationRepository.deleteByProfessorId(userId);
        List<EducationRecord> records = requests.stream().map(r -> {
            EducationRecord record = new EducationRecord();
            record.setProfessorId(userId);
            record.setSchoolName(r.schoolName());
            record.setMajorName(r.majorName());
            record.setDegreeId(r.degreeId());
            record.setStartDate(r.startDate());
            record.setEndDate(r.endDate());
            record.setFullTime(r.fullTime() == null || r.fullTime());
            record.setResearchDirection(r.researchDirection());
            return record;
        }).toList();
        educationRepository.saveAll(records);
    }

    // ---------- 查询 ----------

    @Transactional(readOnly = true)
    public Page<ProfessorSummary> list(Long jobRankId, Pageable pageable) {
        Page<ProfessorProfile> page = jobRankId == null
                ? profileRepository.findByApproved(true, pageable)
                : profileRepository.findByApprovedTrueAndJobRankId(jobRankId, pageable);
        return toSummaries(page);
    }

    @Transactional(readOnly = true)
    public Page<ProfessorSummary> search(String keyword, Pageable pageable) {        if (keyword == null || keyword.isBlank()) {
            return toSummaries(profileRepository.findByApproved(true, pageable));
        }
        return toSummaries(profileRepository.search(keyword.trim(), pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProfessorSummary> pending(Pageable pageable) {
        return toSummaries(profileRepository.findByApproved(false, pageable));
    }

    @Transactional(readOnly = true)
    public ProfessorDetail detail(Long professorId) {
        ProfessorProfile profile = requireApprovedProfile(professorId);
        ProfessorSummary summary = toSummaries(List.of(profile)).get(0);
        return new ProfessorDetail(summary, educations(professorId), reviews(professorId));
    }

    @Transactional(readOnly = true)
    public List<ProfessorDetail.EducationView> educations(Long professorId) {
        Map<Long, String> degreeNames = degreeRepository.findAll().stream()
                .collect(Collectors.toMap(Degree::getId, Degree::getName));
        return educationRepository.findByProfessorIdOrderByStartDateAsc(professorId).stream()
                .map(e -> new ProfessorDetail.EducationView(e.getId(), e.getSchoolName(), e.getMajorName(),
                        e.getDegreeId() == null ? null : degreeNames.get(e.getDegreeId()),
                        e.getStartDate(), e.getEndDate(), e.isFullTime(), e.getResearchDirection()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfessorDetail.ReviewView> reviews(Long professorId) {
        List<ProfessorReview> reviews = reviewRepository.findByProfessorIdOrderByCreatedAtDesc(professorId);
        Map<Long, String> names = userRepository.findAllById(
                        reviews.stream().map(ProfessorReview::getUserId).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(User::getId, this::displayName));
        return reviews.stream().map(r -> new ProfessorDetail.ReviewView(r.getId(), r.getUserId(),
                names.getOrDefault(r.getUserId(), "用户"), r.getRating(), r.getContent(), r.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void addReview(Long userId, Long professorId, ProfessorRequests.ReviewRequest request) {
        if (Objects.equals(userId, professorId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能评价自己");
        }
        requireApprovedProfile(professorId);
        ProfessorReview review = new ProfessorReview();
        review.setProfessorId(professorId);
        review.setUserId(userId);
        review.setRating(request.rating());
        review.setContent(request.content());
        reviewRepository.save(review);
    }

    // ---------- 装配 ----------

    private Page<ProfessorSummary> toSummaries(Page<ProfessorProfile> page) {
        List<ProfessorSummary> summaries = toSummaries(page.getContent());
        return new org.springframework.data.domain.PageImpl<>(summaries, page.getPageable(), page.getTotalElements());
    }

    private List<ProfessorSummary> toSummaries(List<ProfessorProfile> profiles) {
        if (profiles.isEmpty()) {
            return List.of();
        }
        Set<Long> ids = profiles.stream().map(ProfessorProfile::getUserId).collect(Collectors.toSet());
        Map<Long, User> users = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, String> jobRankNames = jobRankRepository.findAll().stream()
                .collect(Collectors.toMap(JobRank::getId, JobRank::getName));

        Map<Long, List<Long>> areaIdsByProfessor = areaRepository.findByProfessorIdIn(ids).stream()
                .collect(Collectors.groupingBy(ProfessorArea::getProfessorId,
                        Collectors.mapping(ProfessorArea::getAreaId, Collectors.toList())));
        Set<Long> allAreaIds = areaIdsByProfessor.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, String> areaNames = consultAreaRepository.findAllById(allAreaIds).stream()
                .collect(Collectors.toMap(ConsultArea::getId, ConsultArea::getName));
        Map<Long, RatingStat> stats = reviewRepository.statsByProfessorIds(ids).stream()
                .collect(Collectors.toMap(RatingStat::professorId, s -> s));

        return profiles.stream().map(p -> {
            User user = users.get(p.getUserId());
            RatingStat stat = stats.get(p.getUserId());
            List<String> names = areaIdsByProfessor.getOrDefault(p.getUserId(), List.of()).stream()
                    .map(areaNames::get).filter(Objects::nonNull).toList();
            return new ProfessorSummary(
                    p.getUserId(),
                    user == null ? "未知教授" : displayName(user),
                    user == null ? null : user.getAvatarFileId(),
                    p.getJobRankId() == null ? null : jobRankNames.get(p.getJobRankId()),
                    p.getIntroduction(),
                    p.getConsultPrice(),
                    names,
                    stat == null ? 0d : stat.average(),
                    stat == null ? 0L : stat.count());
        }).toList();
    }

    private void applyFields(ProfessorProfile profile, Long jobRankId, String introduction,
                             BigDecimal consultPrice, Long cvFileId) {
        if (jobRankId != null) {
            profile.setJobRankId(jobRankId);
        }
        if (introduction != null) {
            profile.setIntroduction(introduction);
        }
        if (consultPrice != null) {
            if (consultPrice.signum() < 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "咨询价格不能为负");
            }
            profile.setConsultPrice(consultPrice);
        }
        if (cvFileId != null) {
            profile.setCvFileId(cvFileId);
        }
    }

    private ProfessorProfile requireProfile(Long userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFESSOR_PROFILE_NOT_FOUND));
    }

    private ProfessorProfile requireApprovedProfile(Long userId) {
        ProfessorProfile profile = requireProfile(userId);
        if (!profile.isApproved()) {
            throw new BusinessException(ErrorCode.NOT_APPROVED_PROFESSOR);
        }
        return profile;
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private String displayName(User user) {
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}

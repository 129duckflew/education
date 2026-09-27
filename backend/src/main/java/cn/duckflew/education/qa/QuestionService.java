package cn.duckflew.education.qa;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.messaging.NotificationService;
import cn.duckflew.education.messaging.NotificationType;
import cn.duckflew.education.order.OrderService;
import cn.duckflew.education.order.PayOrder;
import cn.duckflew.education.professor.ProfessorProfile;
import cn.duckflew.education.professor.ProfessorProfileRepository;
import cn.duckflew.education.qa.dto.QuestionCard;
import cn.duckflew.education.qa.dto.QuestionDetail;
import cn.duckflew.education.qa.dto.SubmitPaidQuestionRequest;
import cn.duckflew.education.qa.dto.SubmitQuestionRequest;
import cn.duckflew.education.taxonomy.ConsultAreaService;
import cn.duckflew.education.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionAreaRepository questionAreaRepository;
    private final QuestionProfessorRepository questionProfessorRepository;
    private final QuestionImageRepository questionImageRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final ProfessorProfileRepository professorProfileRepository;
    private final cn.duckflew.education.taxonomy.ConsultAreaRepository consultAreaRepository;
    private final ConsultAreaService consultAreaService;
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final QuestionAssembler assembler;

    public QuestionService(QuestionRepository questionRepository,
                           QuestionAreaRepository questionAreaRepository,
                           QuestionProfessorRepository questionProfessorRepository,
                           QuestionImageRepository questionImageRepository,
                           QuestionLikeRepository questionLikeRepository,
                           ProfessorProfileRepository professorProfileRepository,
                           cn.duckflew.education.taxonomy.ConsultAreaRepository consultAreaRepository,
                           ConsultAreaService consultAreaService,
                           OrderService orderService,
                           NotificationService notificationService,
                           QuestionAssembler assembler) {
        this.questionRepository = questionRepository;
        this.questionAreaRepository = questionAreaRepository;
        this.questionProfessorRepository = questionProfessorRepository;
        this.questionImageRepository = questionImageRepository;
        this.questionLikeRepository = questionLikeRepository;
        this.professorProfileRepository = professorProfileRepository;
        this.consultAreaRepository = consultAreaRepository;
        this.consultAreaService = consultAreaService;
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.assembler = assembler;
    }

    @Transactional
    public Question submitFree(Long userId, SubmitQuestionRequest request) {
        validateAreas(request.areaIds());
        request.professorIds().forEach(this::requireApprovedProfessor);
        Question question = createQuestion(userId, request.title(), request.description(), null);
        attachAreas(question.getId(), request.areaIds());
        attachImages(question.getId(), request.imageFileIds());
        request.professorIds().forEach(professorId -> {
            attachProfessor(question.getId(), professorId);
            notificationService.notify(professorId, userId, NotificationType.QUESTION_RECEIVED,
                    question.getId(), userId);
        });
        return question;
    }

    @Transactional
    public PayOrder submitPaid(Long userId, SubmitPaidQuestionRequest request) {
        validateAreas(request.areaIds());
        ProfessorProfile profile = requireApprovedProfessor(request.professorId());
        BigDecimal price = profile.getConsultPrice();
        PayOrder order = orderService.create(userId, price, "付费咨询订单");

        Question question = createQuestion(userId, request.title(), request.description(), order.getId());
        attachAreas(question.getId(), request.areaIds());
        attachImages(question.getId(), request.imageFileIds());
        attachProfessor(question.getId(), request.professorId());
        notificationService.notify(request.professorId(), userId, NotificationType.QUESTION_RECEIVED,
                question.getId(), userId);
        return order;
    }

    @Transactional
    public Question audit(Long questionId, QuestionStatus status) {
        if (status == QuestionStatus.AUDITING || status == QuestionStatus.PRIVATE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "审核状态非法");
        }
        Question question = getRequired(questionId);
        question.setStatus(status);
        questionRepository.save(question);
        NotificationType type = switch (status) {
            case NORMAL -> NotificationType.QUESTION_APPROVED;
            case REJECTED -> NotificationType.QUESTION_REJECTED;
            case FORBIDDEN -> NotificationType.QUESTION_FORBIDDEN;
            default -> NotificationType.SYSTEM;
        };
        notificationService.notify(question.getAuthorId(), null, type, question.getId(), null);
        return question;
    }

    @Transactional(readOnly = true)
    public QuestionDetail detail(Long questionId, Long viewerId, UserRole viewerRole) {
        Question question = getRequired(questionId);
        assertVisible(question, viewerId, viewerRole);
        return assembler.toDetail(question, viewerId);
    }

    @Transactional(readOnly = true)
    public Page<QuestionCard> listMine(Long userId, Pageable pageable) {
        return toCards(questionRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable), userId);
    }

    @Transactional(readOnly = true)
    public List<QuestionCard> listLiked(Long userId) {
        List<Long> ids = questionLikeRepository.findByUserId(userId).stream()
                .map(QuestionLike::getQuestionId).toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        return assembler.toCards(questionRepository.findAllById(ids), userId);
    }

    @Transactional(readOnly = true)
    public Page<QuestionCard> recommendByAreas(Collection<Long> areaIds, Long viewerId, Pageable pageable) {
        Set<Long> subtree = consultAreaService.subtreeIds(areaIds);
        if (subtree.isEmpty()) {
            return search(null, pageable);
        }
        Page<Question> page = questionRepository.findRecommended(subtree, QuestionStatus.NORMAL, pageable);
        return toCards(page, viewerId);
    }

    @Transactional(readOnly = true)
    public Page<QuestionCard> search(String keyword, Pageable pageable) {
        Page<Question> page = (keyword == null || keyword.isBlank())
                ? questionRepository.findByStatus(QuestionStatus.NORMAL, pageable)
                : questionRepository.searchNormal(keyword.trim(), pageable);
        return toCards(page, null);
    }

    @Transactional(readOnly = true)
    public Page<Question> adminSearch(QuestionStatus status, Pageable pageable) {
        return status == null ? questionRepository.findAll(pageable)
                : questionRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public List<QuestionCard> detailCards(List<Question> questions) {
        return assembler.toCards(questions, null);
    }

    @Transactional
    public void like(Long userId, Long questionId) {
        Question question = getRequired(questionId);
        if (!questionLikeRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            QuestionLike like = new QuestionLike();
            like.setUserId(userId);
            like.setQuestionId(questionId);
            questionLikeRepository.save(like);
            notificationService.notify(question.getAuthorId(), userId,
                    NotificationType.QUESTION_LIKED, questionId, userId);
        }
    }

    @Transactional
    public void unlike(Long userId, Long questionId) {
        questionLikeRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }

    public Question getRequired(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    private Page<QuestionCard> toCards(Page<Question> page, Long viewerId) {
        var cards = assembler.toCards(page.getContent(), viewerId);
        return new org.springframework.data.domain.PageImpl<>(cards, page.getPageable(), page.getTotalElements());
    }

    private Question createQuestion(Long userId, String title, String description, Long orderId) {
        Question question = new Question();
        question.setAuthorId(userId);
        question.setTitle(title);
        question.setDescription(description);
        question.setStatus(QuestionStatus.AUDITING);
        question.setOrderId(orderId);
        return questionRepository.save(question);
    }

    private void attachAreas(Long questionId, List<Long> areaIds) {
        areaIds.stream().distinct().forEach(areaId -> {
            QuestionArea entity = new QuestionArea();
            entity.setQuestionId(questionId);
            entity.setAreaId(areaId);
            questionAreaRepository.save(entity);
        });
    }

    private void attachImages(Long questionId, List<Long> imageFileIds) {
        if (imageFileIds == null) {
            return;
        }
        int order = 0;
        for (Long fileId : imageFileIds) {
            QuestionImage image = new QuestionImage();
            image.setQuestionId(questionId);
            image.setFileId(fileId);
            image.setSortOrder(order++);
            questionImageRepository.save(image);
        }
    }

    private void attachProfessor(Long questionId, Long professorId) {
        QuestionProfessor entity = new QuestionProfessor();
        entity.setQuestionId(questionId);
        entity.setProfessorId(professorId);
        questionProfessorRepository.save(entity);
    }

    private ProfessorProfile requireApprovedProfessor(Long professorId) {
        ProfessorProfile profile = professorProfileRepository.findById(professorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PROFESSOR));
        if (!profile.isApproved()) {
            throw new BusinessException(ErrorCode.NOT_APPROVED_PROFESSOR);
        }
        return profile;
    }

    private void validateAreas(List<Long> areaIds) {
        for (Long areaId : areaIds) {
            if (!consultAreaRepository.existsById(areaId)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "领域不存在: " + areaId);
            }
        }
    }

    private void assertVisible(Question question, Long viewerId, UserRole viewerRole) {
        if (question.getStatus() == QuestionStatus.NORMAL) {
            return;
        }
        boolean isAuthor = viewerId != null && viewerId.equals(question.getAuthorId());
        boolean isAdmin = viewerRole == UserRole.ADMIN;
        if (!isAuthor && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "该问题不可见");
        }
    }
}

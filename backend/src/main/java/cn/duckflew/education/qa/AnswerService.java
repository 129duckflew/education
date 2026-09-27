package cn.duckflew.education.qa;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.messaging.NotificationService;
import cn.duckflew.education.messaging.NotificationType;
import cn.duckflew.education.qa.dto.AnswerRequest;
import cn.duckflew.education.qa.dto.AnswerView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerCollectRepository answerCollectRepository;
    private final NotificationService notificationService;
    private final QuestionAssembler assembler;

    public AnswerService(AnswerRepository answerRepository,
                         QuestionRepository questionRepository,
                         AnswerLikeRepository answerLikeRepository,
                         AnswerCollectRepository answerCollectRepository,
                         NotificationService notificationService,
                         QuestionAssembler assembler) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.answerLikeRepository = answerLikeRepository;
        this.answerCollectRepository = answerCollectRepository;
        this.notificationService = notificationService;
        this.assembler = assembler;
    }

    /**
     * 教授作答。同一教授对同一问题只保留一条回答，重复提交为修改（修复旧版只改时间不改内容的缺陷）。
     */
    @Transactional
    public Answer publish(Long professorId, AnswerRequest request) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        Answer answer = answerRepository
                .findByQuestionIdAndProfessorId(request.questionId(), professorId)
                .orElse(null);

        if (answer != null) {
            answer.setContent(request.content());
            return answerRepository.save(answer);
        }

        answer = new Answer();
        answer.setQuestionId(question.getId());
        answer.setProfessorId(professorId);
        answer.setContent(request.content());
        answer.setStatus(AnswerStatus.NORMAL);
        answerRepository.save(answer);
        notificationService.notify(question.getAuthorId(), professorId,
                NotificationType.ANSWER_RECEIVED, answer.getId(), professorId);
        return answer;
    }

    @Transactional(readOnly = true)
    public List<AnswerView> listByQuestion(Long questionId, Long viewerId) {
        return assembler.toAnswerViews(
                answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId), viewerId);
    }

    @Transactional(readOnly = true)
    public List<AnswerView> listCollected(Long userId) {
        List<Long> answerIds = answerCollectRepository.findByUserId(userId).stream()
                .map(AnswerCollect::getAnswerId).toList();
        if (answerIds.isEmpty()) {
            return List.of();
        }
        return assembler.toAnswerViews(answerRepository.findAllById(answerIds), userId);
    }

    @Transactional
    public void like(Long userId, Long answerId) {
        Answer answer = getRequired(answerId);
        if (!answerLikeRepository.existsByUserIdAndAnswerId(userId, answerId)) {
            AnswerLike like = new AnswerLike();
            like.setUserId(userId);
            like.setAnswerId(answerId);
            answerLikeRepository.save(like);
            notificationService.notify(answer.getProfessorId(), userId,
                    NotificationType.ANSWER_LIKED, answerId, userId);
        }
    }

    @Transactional
    public void unlike(Long userId, Long answerId) {
        answerLikeRepository.deleteByUserIdAndAnswerId(userId, answerId);
    }

    @Transactional
    public void collect(Long userId, Long answerId) {
        Answer answer = getRequired(answerId);
        if (!answerCollectRepository.existsByUserIdAndAnswerId(userId, answerId)) {
            AnswerCollect collect = new AnswerCollect();
            collect.setUserId(userId);
            collect.setAnswerId(answerId);
            answerCollectRepository.save(collect);
            notificationService.notify(answer.getProfessorId(), userId,
                    NotificationType.ANSWER_COLLECTED, answerId, userId);
        }
    }

    @Transactional
    public void uncollect(Long userId, Long answerId) {
        answerCollectRepository.deleteByUserIdAndAnswerId(userId, answerId);
    }

    @Transactional
    public void ban(Long answerId) {
        Answer answer = getRequired(answerId);
        answer.setStatus(AnswerStatus.FORBIDDEN);
        answerRepository.save(answer);
    }

    private Answer getRequired(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));
    }
}

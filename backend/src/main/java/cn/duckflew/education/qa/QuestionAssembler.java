package cn.duckflew.education.qa;

import cn.duckflew.education.qa.dto.AnswerView;
import cn.duckflew.education.qa.dto.QuestionCard;
import cn.duckflew.education.qa.dto.QuestionDetail;
import cn.duckflew.education.comment.CommentRepository;
import cn.duckflew.education.comment.CommentTargetType;
import cn.duckflew.education.taxonomy.ConsultArea;
import cn.duckflew.education.taxonomy.ConsultAreaRepository;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 问答读模型装配器：用批量查询替代逐条查询，消除旧版 N+1。
 */
@Component
public class QuestionAssembler {

    private final QuestionAreaRepository questionAreaRepository;
    private final QuestionImageRepository questionImageRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final AnswerRepository answerRepository;
    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerCollectRepository answerCollectRepository;
    private final ConsultAreaRepository consultAreaRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public QuestionAssembler(QuestionAreaRepository questionAreaRepository,
                             QuestionImageRepository questionImageRepository,
                             QuestionLikeRepository questionLikeRepository,
                             AnswerRepository answerRepository,
                             AnswerLikeRepository answerLikeRepository,
                             AnswerCollectRepository answerCollectRepository,
                             ConsultAreaRepository consultAreaRepository,
                             UserRepository userRepository,
                             CommentRepository commentRepository) {
        this.questionAreaRepository = questionAreaRepository;
        this.questionImageRepository = questionImageRepository;
        this.questionLikeRepository = questionLikeRepository;
        this.answerRepository = answerRepository;
        this.answerLikeRepository = answerLikeRepository;
        this.answerCollectRepository = answerCollectRepository;
        this.consultAreaRepository = consultAreaRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public List<QuestionCard> toCards(List<Question> questions, Long viewerId) {
        if (questions.isEmpty()) {
            return List.of();
        }
        List<Long> questionIds = questions.stream().map(Question::getId).toList();

        Map<Long, List<Long>> areaIdsByQuestion = questionAreaRepository.findByQuestionIdIn(questionIds).stream()
                .collect(Collectors.groupingBy(QuestionArea::getQuestionId,
                        Collectors.mapping(QuestionArea::getAreaId, Collectors.toList())));
        Set<Long> allAreaIds = areaIdsByQuestion.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        Map<Long, String> areaNames = consultAreaRepository.findAllById(allAreaIds).stream()
                .collect(Collectors.toMap(ConsultArea::getId, ConsultArea::getName));

        Map<Long, List<Long>> imagesByQuestion = questionImageRepository.findByQuestionIdIn(questionIds).stream()
                .collect(Collectors.groupingBy(QuestionImage::getQuestionId,
                        Collectors.mapping(QuestionImage::getFileId, Collectors.toList())));

        Map<Long, Long> likeCounts = toCountMap(questionLikeRepository.countByQuestionIds(questionIds));
        Map<Long, Long> answerCounts = toCountMap(answerRepository.countByQuestionIds(questionIds));
        Map<Long, Long> commentCounts = toCountMap(
                commentRepository.countByTargetIds(CommentTargetType.QUESTION, questionIds));

        Set<Long> likedByViewer = viewerId == null ? Set.of()
                : questionLikeRepository.findByUserIdAndQuestionIdIn(viewerId, questionIds).stream()
                .map(QuestionLike::getQuestionId).collect(Collectors.toSet());

        Set<Long> authorIds = questions.stream().map(Question::getAuthorId).collect(Collectors.toSet());
        Map<Long, String> authorNames = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, this::displayName));

        return questions.stream().map(q -> {
            List<Long> areaIds = areaIdsByQuestion.getOrDefault(q.getId(), List.of());
            List<String> names = areaIds.stream().map(areaNames::get).filter(Objects::nonNull).toList();
            return new QuestionCard(
                    q.getId(), q.getTitle(), q.getDescription(), q.getStatus(),
                    q.getAuthorId(), authorNames.getOrDefault(q.getAuthorId(), "未知用户"),
                    areaIds, names,
                    imagesByQuestion.getOrDefault(q.getId(), List.of()),
                    likeCounts.getOrDefault(q.getId(), 0L),
                    answerCounts.getOrDefault(q.getId(), 0L),
                    commentCounts.getOrDefault(q.getId(), 0L),
                    likedByViewer.contains(q.getId()),
                    q.getCreatedAt());
        }).toList();
    }

    public QuestionDetail toDetail(Question question, Long viewerId) {
        QuestionCard card = toCards(List.of(question), viewerId).get(0);
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtAsc(question.getId());
        return new QuestionDetail(card, toAnswerViews(answers, viewerId));
    }

    public List<AnswerView> toAnswerViews(List<Answer> answers, Long viewerId) {
        if (answers.isEmpty()) {
            return List.of();
        }
        List<Long> answerIds = answers.stream().map(Answer::getId).toList();
        Map<Long, Long> likeCounts = toCountMap(answerLikeRepository.countByAnswerIds(answerIds));
        Map<Long, Long> collectCounts = toCountMap(answerCollectRepository.countByAnswerIds(answerIds));
        Map<Long, Long> commentCounts = toCountMap(
                commentRepository.countByTargetIds(CommentTargetType.ANSWER, answerIds));

        Set<Long> liked = viewerId == null ? Set.of()
                : answerLikeRepository.findByUserIdAndAnswerIdIn(viewerId, answerIds).stream()
                .map(AnswerLike::getAnswerId).collect(Collectors.toSet());
        Set<Long> collected = viewerId == null ? Set.of()
                : answerCollectRepository.findByUserIdAndAnswerIdIn(viewerId, answerIds).stream()
                .map(AnswerCollect::getAnswerId).collect(Collectors.toSet());

        Set<Long> professorIds = answers.stream().map(Answer::getProfessorId).collect(Collectors.toSet());
        Map<Long, String> professorNames = userRepository.findAllById(professorIds).stream()
                .collect(Collectors.toMap(User::getId, this::displayName));

        return answers.stream().map(a -> new AnswerView(
                a.getId(), a.getProfessorId(), professorNames.getOrDefault(a.getProfessorId(), "未知教授"),
                a.getContent(),
                likeCounts.getOrDefault(a.getId(), 0L),
                collectCounts.getOrDefault(a.getId(), 0L),
                commentCounts.getOrDefault(a.getId(), 0L),
                liked.contains(a.getId()),
                collected.contains(a.getId()),
                a.getCreatedAt())).toList();
    }

    private Map<Long, Long> toCountMap(List<IdCount> counts) {
        return counts.stream().collect(Collectors.toMap(IdCount::id, IdCount::count));
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

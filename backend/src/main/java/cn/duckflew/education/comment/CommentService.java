package cn.duckflew.education.comment;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.messaging.NotificationAnchor;
import cn.duckflew.education.messaging.NotificationService;
import cn.duckflew.education.messaging.NotificationType;
import cn.duckflew.education.qa.Answer;
import cn.duckflew.education.qa.AnswerRepository;
import cn.duckflew.education.qa.Question;
import cn.duckflew.education.qa.QuestionRepository;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository,
                          QuestionRepository questionRepository,
                          AnswerRepository answerRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Long create(Long userId, CommentDtos.CreateRequest request) {
        Target target = resolveTarget(request.targetType(), request.targetId());

        Comment comment = new Comment();
        comment.setTargetType(request.targetType());
        comment.setTargetId(request.targetId());
        comment.setUserId(userId);
        comment.setContent(request.content());

        Long parentAuthorId = null;
        if (request.parentId() != null) {
            Comment parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "回复的评论不存在"));
            if (parent.getTargetType() != request.targetType()
                    || !parent.getTargetId().equals(request.targetId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "回复的评论与目标不匹配");
            }
            comment.setParentId(parent.getId());
            parentAuthorId = parent.getUserId();
        }
        commentRepository.save(comment);

        NotificationAnchor anchor = request.targetType() == CommentTargetType.QUESTION
                ? NotificationAnchor.QUESTION_COMMENT
                : NotificationAnchor.ANSWER_COMMENT;
        if (parentAuthorId != null) {
            notificationService.notify(parentAuthorId, userId, NotificationType.REPLY_TO_COMMENT,
                    target.questionId(), userId, anchor, comment.getId(), target.answerId());
        } else if (target.authorId() != null) {
            NotificationType type = request.targetType() == CommentTargetType.QUESTION
                    ? NotificationType.COMMENT_ON_QUESTION
                    : NotificationType.COMMENT_ON_ANSWER;
            notificationService.notify(target.authorId(), userId, type,
                    target.questionId(), userId, anchor, comment.getId(), target.answerId());
        }
        return comment.getId();
    }

    @Transactional(readOnly = true)
    public List<CommentDtos.CommentView> list(CommentTargetType targetType, Long targetId) {
        List<Comment> all = commentRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtAsc(targetType, targetId);
        if (all.isEmpty()) {
            return List.of();
        }
        Map<Long, User> users = userRepository.findAllById(
                        all.stream().map(Comment::getUserId).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<Long, List<Comment>> byParent = new LinkedHashMap<>();
        List<Comment> roots = new ArrayList<>();
        for (Comment c : all) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                byParent.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c);
            }
        }
        return roots.stream().map(c -> toView(c, users, byParent)).toList();
    }

    @Transactional
    public void delete(Long userId, boolean admin, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (!admin && !comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该评论");
        }
        commentRepository.delete(comment);
    }

    private CommentDtos.CommentView toView(Comment c, Map<Long, User> users,
                                           Map<Long, List<Comment>> byParent) {
        User user = users.get(c.getUserId());
        String name = user == null ? "用户" : displayName(user);
        String avatar = user == null || user.getAvatarFileId() == null
                ? null : "/api/files/" + user.getAvatarFileId();
        List<CommentDtos.CommentView> replies = byParent.getOrDefault(c.getId(), List.of()).stream()
                .map(child -> toView(child, users, byParent)).toList();
        return new CommentDtos.CommentView(c.getId(), c.getTargetType(), c.getTargetId(),
                c.getUserId(), name, avatar, c.getContent(), c.getParentId(), c.getCreatedAt(), replies);
    }

    private Target resolveTarget(CommentTargetType type, Long targetId) {
        if (type == CommentTargetType.QUESTION) {
            Question question = questionRepository.findById(targetId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
            return new Target(question.getAuthorId(), question.getId(), null);
        }
        Answer answer = answerRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));
        return new Target(answer.getProfessorId(), answer.getQuestionId(), answer.getId());
    }

    /** 评论目标解析结果：作者、所属问题、所属回答（问题评论时为 null）。 */
    private record Target(Long authorId, Long questionId, Long answerId) {
    }

    private String displayName(User user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        if (user.getRealName() != null && !user.getRealName().isBlank()) {
            return user.getRealName();
        }
        return user.getUsername() == null ? "用户" + user.getId() : user.getUsername();
    }
}

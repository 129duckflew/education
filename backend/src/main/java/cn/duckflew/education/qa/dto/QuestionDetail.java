package cn.duckflew.education.qa.dto;

import java.util.List;

public record QuestionDetail(QuestionCard question, List<AnswerView> answers) {
}

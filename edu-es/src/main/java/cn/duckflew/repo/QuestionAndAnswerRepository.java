package cn.duckflew.repo;

import cn.duckflew.document.QuestionAndAnswerDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface QuestionAndAnswerRepository extends ElasticsearchRepository<QuestionAndAnswerDoc,Integer>
{

    Page<QuestionAndAnswerDoc> findQuestionAndAnswerByQuestionDescOrQuestionTitleOrAnswerText(String questionDesc, String questionTitle, String answerText, Pageable page);
    Page<QuestionAndAnswerDoc> findQuestionAndAnswerByQuestionDescOrQuestionTitle(String questionDesc, String questionTitle, Pageable page);
    Page<QuestionAndAnswerDoc> findQuestionAndAnswerByAnswerText( String answerText, Pageable page);
}

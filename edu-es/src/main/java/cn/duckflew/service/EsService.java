package cn.duckflew.service;

import cn.duckflew.document.ProfessorInfoDoc;
import cn.duckflew.document.QuestionAndAnswerDoc;
import cn.duckflew.repo.ProfessorInfoRepository;
import cn.duckflew.repo.QuestionAndAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class EsService
{
    @Autowired
    QuestionAndAnswerRepository questionAndAnswerRepository;
    public Page<QuestionAndAnswerDoc> simpleSearchPage(Integer pageNum, Integer pageSize, String keyword)
    {
        Pageable page=PageRequest.of(pageNum, pageSize);
        Page<QuestionAndAnswerDoc> res = questionAndAnswerRepository.findQuestionAndAnswerByQuestionDescOrQuestionTitleOrAnswerText(keyword, keyword, keyword, page);
        return res;
    }


    @Autowired
    ProfessorInfoRepository professorInfoRepository;

    public void saveProfessorInfoDoc(ProfessorInfoDoc professorDoc)
    {
        professorInfoRepository.save(professorDoc);
    }
    public ProfessorInfoDoc getProfessorInfoById(Integer userId)
    {
        return professorInfoRepository.findById(userId).get();
    }

    public void  deleteProfessorInfoById(Integer userId)
    {
         professorInfoRepository.deleteById(userId);
    }
}

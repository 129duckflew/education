package cn.duckflew.repo;

import cn.duckflew.document.ProfessorInfoDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProfessorInfoRepository extends ElasticsearchRepository<ProfessorInfoDoc,Integer>
{

    Page<ProfessorInfoDoc> findAllByRealNameOrProfessorIntroductionOrAnswerAreaNameListOrSchoolNameList(
            String realName,
            String professorIntroduction,
            String answerAreaName,
            String schoolName,
            Pageable page
    );
}

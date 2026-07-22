package cn.duckflew.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "professor_info",createIndex = true )
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfessorInfoDoc
{
    @Id
    private Integer professorId;
    @Field(type= FieldType.Keyword)
    private String realName;
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private String professorIntroduction;
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private List<String> answerAreaNameList;
    @Field(type=FieldType.Keyword)
    private List<String> schoolNameList;
    @Field(type=FieldType.Keyword)
    private String jobRankName;
    @Field(type=FieldType.Keyword)
    private String avatar;

}

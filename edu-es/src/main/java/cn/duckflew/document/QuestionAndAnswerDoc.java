package cn.duckflew.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "qa",createIndex = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionAndAnswerDoc
{
    @Id
    private Integer id;
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private String questionTitle;
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private String questionDesc;
    @Field(type=FieldType.Text,analyzer = "ik_max_word")
    private String answerText;
    @Field(type=FieldType.Keyword)
    private String professorName;
    @Field(type=FieldType.Integer)
    private Integer questionId;
    @Field(type=FieldType.Integer)
    private Integer answerId;
}

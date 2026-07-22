package cn.duckflew.vo;

import cn.duckflew.entity.FileRecord;
import cn.duckflew.entity.professor.StudyResource;
import lombok.Getter;
import lombok.Setter;

public class StudyResourceVo extends StudyResource
{

    /**
     * 上传者头像
     */
    @Getter
    @Setter
    private String userAvatar;
    /**
     * 上传者昵称
     */
    @Getter
    @Setter
    private String userNickname;

    @Getter
    @Setter
    private FileRecord fileRecord;
}

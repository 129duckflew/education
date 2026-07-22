package cn.duckflew.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("file_record")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileRecord
{
    @TableId
    private String id;
    private String filePath;
    private Integer length;
    private String bucketName;
    private Date createTime;
    private String contentType;
    private String fileName;
}

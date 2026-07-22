package cn.duckflew.service.file;

import cn.dev33.satoken.util.SaResult;
import cn.duckflew.config.MinioConfigProperties;
import cn.duckflew.entity.FileRecord;
import cn.duckflew.entity.user.BaseUser;
import cn.duckflew.exception.BadFileFormatException;
import cn.duckflew.exception.FileIdInvalidException;
import cn.duckflew.exception.FileUploadException;
import cn.duckflew.mapper.BaseUserMapper;
import cn.duckflew.mapper.FileRecordMapper;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FileService
{
    @Autowired
    FileRecordMapper fileRecordMapper;
    @Autowired
    MinioConfigProperties minioConfigProperties;
    @Autowired
    MinioClient minioClient;
    @Value("${file.upload.avatar.path}")
    private String UPLOAD_AVATAR_FOLDER;
    @Value("${file.upload.cv.path}")
    private String UPLOAD_CV_FOLDER;
    @Value("${file.upload.news.cover.path}")
    private String NEWS_COVER_PATH;
    @Value("${file.upload.study-resource.path}")
    private String STUDY_RESOURCE_PATH;
    @Autowired
    BaseUserMapper baseUserMapper;
    /**
     * 直接上传文件 返回文件id
     * @param file
     * @param userId
     * @return
     * @throws IOException
     */
    public String  uploadCVFile(MultipartFile file,Integer userId) throws FileUploadException
    {
        FileRecord uploadRes = putObject(UPLOAD_CV_FOLDER + userId + "/", file);
        if (uploadRes==null)throw new FileUploadException("简历文件上传异常",userId);
        log.info("userId:{}上传简历文件,fileId={}",userId,uploadRes.getId());
        return uploadRes.getId();
    }
    public String  uploadStudyResource(MultipartFile file,Integer userId) throws FileUploadException
    {
        FileRecord uploadRes = putObject(STUDY_RESOURCE_PATH + userId + "/", file);
        if (uploadRes==null)throw new FileUploadException("简历文件上传异常",userId);
        log.info("userId:{}上传学习资料文件成功,fileId={}",userId,uploadRes.getId());
        return uploadRes.getId();
    }
    public void checkImgFormat(String filename)
    {
        boolean matches = Pattern.matches("^(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", filename);
        if (!matches)
        {
            int index = filename.lastIndexOf(".");
            String badFormat=filename.substring(index);
            throw new BadFileFormatException("图片不符合格式",badFormat);
        }
    }

    public String uploadAvatarFile(MultipartFile file, Integer userId) throws FileUploadException
    {
        checkImgFormat(file.getOriginalFilename());
        FileRecord uploadRes = putObject(UPLOAD_AVATAR_FOLDER + userId + "/", file);
        if (uploadRes==null)throw new FileUploadException("头像上传异常",userId);
        log.info("userId:{}上传头像,fileId={}",userId,uploadRes.getId());
        return uploadRes.getId();
    }
    public String uploadNewsCoverFile(MultipartFile file, Integer adminId) throws FileUploadException
    {
        checkImgFormat(file.getOriginalFilename());
        FileRecord uploadRes = putObject(NEWS_COVER_PATH + adminId + "/", file);
        if (uploadRes==null)throw new FileUploadException("封面文件上传异常",adminId);
        log.info("adminId:{}上传封面文件,fileId={}",adminId,uploadRes.getId());
        return uploadRes.getId();
    }
    public String uploadAdminAvatar(MultipartFile file, Integer adminId) throws FileUploadException
    {
        checkImgFormat(file.getOriginalFilename());
        FileRecord uploadRes = putObject(NEWS_COVER_PATH + adminId + "/", file);
        if (uploadRes==null)throw new FileUploadException("管理员头像上传异常",adminId);
        log.info("adminId:{}上传头像,fileId={}",adminId,uploadRes.getId());
        return uploadRes.getId();
    }

    private FileRecord  putObject(String dirs,MultipartFile file,boolean createIfNotExistBucket) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException
    {
        FileRecord res = new FileRecord();
        String bucketName = minioConfigProperties.getBucketName();
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String path= originalFilename;
        res.setFilePath(dirs);
        res.setBucketName(bucketName);
        res.setContentType(contentType);
        res.setCreateTime(new Date());
        res.setFileName(originalFilename);
        if (StringUtils.isNotBlank(dirs))
        {
            path= Paths.get(dirs,originalFilename).toString();
        }
        try
        {
            InputStream fileStream = file.getInputStream();
            if (createIfNotExistBucket)
            {
                createIfNotExistBucket(bucketName);
            }
            Integer length = fileStream.available();
            res.setLength(length);
            log.info("存入bucket的Object参数就是path:{}",path);
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucketName).object(path).contentType(contentType).stream(fileStream, length, -1).build();
            minioClient.putObject(args);
        } catch (Exception e)
        {
            throw e;
        }
        return res;
    }
    public FileRecord putObject(String dirs, MultipartFile file)
    {
        FileRecord fileRecord = null;
        try
        {
            fileRecord = putObject(dirs, file, true);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (InvalidResponseException e)
        {
            e.printStackTrace();
        } catch (InsufficientDataException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        } catch (ServerException e)
        {
            e.printStackTrace();
        } catch (InternalException e)
        {
            e.printStackTrace();
        } catch (XmlParserException e)
        {
            e.printStackTrace();
        } catch (ErrorResponseException e)
        {
            e.printStackTrace();
        }
        if (fileRecord!=null)
        {
            fileRecord.setId(UUID.randomUUID().toString());
            fileRecordMapper.insert(fileRecord);
        }
        return fileRecord;
    }
    public void createIfNotExistBucket(String bucketName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException
    {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    public void getObject(String fileId, HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, XmlParserException, ErrorResponseException
    {
        FileRecord fileRecord = fileRecordMapper.selectById(fileId);
        log.info("获取minio文件,fileId={}",fileId);
        log.info("fileRecord数据:{}",fileRecord);
        if (fileRecord==null)throw new FileIdInvalidException("文件id无效",fileId);
        GetObjectArgs objectArgs= GetObjectArgs.builder()
                .bucket(fileRecord.getBucketName())
                .object(Paths.get(fileRecord.getFilePath(),fileRecord.getFileName()).toString())
                .build();
        try (InputStream input = minioClient.getObject(objectArgs); OutputStream outputStream = response.getOutputStream()) {
            response.setContentType(fileRecord.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(fileRecord.getLength()));
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileRecord.getFileName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            outputStream.write(input.readAllBytes());
            outputStream.flush();
        } catch (Exception e) {
            // 建议包装成自定义异常，以便自定义异常处理捕获到
            throw e;
        }
    }
}

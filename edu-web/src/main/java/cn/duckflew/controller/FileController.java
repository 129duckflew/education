package cn.duckflew.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.duckflew.exception.FileUploadException;
import cn.duckflew.service.file.FileService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 文件访问相关接口
 */
@RestController
@RequestMapping("/file")
public class FileController
{

    @Autowired
    FileService fileService;

    /**
     * 根据文件id获取文件,所有的文件访问都通过这个接口
     * @param fileId 文件id
     * @param response 不是参数,可以忽略
     * @throws IOException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws InsufficientDataException
     * @throws NoSuchAlgorithmException
     * @throws ServerException
     * @throws ErrorResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @GetMapping("/download/{fileId}")
    public void downloadFile(
            @NotBlank(message = "fileId不能为空")
            @PathVariable String fileId,
            HttpServletResponse response
    ) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InternalException
    {
        fileService.getObject(fileId, response);
    }

    /**
     * 教授简历上传,调用这个接口会上传头像文件返回文件id，获取id之后再调用指定的接口发生作用
     * @param file 简历文件
     * @return
     */
    @PostMapping("/upload/cv")
    @SaCheckLogin
    public SaResult uploadCVFile(@NotNull(message = "文件不能为空") @RequestPart MultipartFile file) throws FileUploadException
    {
        Integer userId= StpUtil.getLoginIdAsInt();
        String fileId = fileService.uploadCVFile(file, userId);
        return SaResult.ok().setMsg("上传简历文件成功").setData(fileId);
    }
    /**
     * 上传学习资源
     * @param file 文件
     * @return
     */
    @PostMapping("/upload/studyResource")
    @SaCheckLogin
    public SaResult uploadStudyResource(@NotNull(message = "文件不能为空") @RequestPart MultipartFile file) throws FileUploadException
    {
        Integer userId= StpUtil.getLoginIdAsInt();
        String fileId = fileService.uploadStudyResource(file, userId);
        return SaResult.ok().setMsg("上传学习资料文件成功").setData(fileId);
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @return
     * @apiNote 调用这个接口会上传头像文件返回文件id，获取id之后再调用指定的接口发生作用
     * 获取用户头像的方法: 通过/profile接口获取个人信息,得到avatar字段
     */
    @PostMapping("/upload/avatar")
    @SaCheckLogin
    public SaResult uploadAvatar(@NotNull(message = "头像文件不能为空") @RequestPart MultipartFile file) throws FileUploadException
    {
        Integer userId=StpUtil.getLoginIdAsInt();
        String fileId = fileService.uploadAvatarFile(file, userId);
        return SaResult.ok().setMsg("上传头像文件成功").setData(fileId);
    }


}

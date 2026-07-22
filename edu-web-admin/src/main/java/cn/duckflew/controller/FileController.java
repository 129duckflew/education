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
 * 管理员后台:文件访问相关接口
 */
@RestController
@RequestMapping("/file")
public class FileController
{
    @Autowired
    FileService fileService;
    /**
     * 上传资讯文章封面
     * @param file 封面文件
     * @return
     * @apiNote 只上传文件不修改数据
     */
    @PostMapping("/upload/cover")
    @SaCheckLogin
    public SaResult uploadCover(@NotNull(message = "封面文件不能为空") @RequestPart MultipartFile file) throws FileUploadException
    {
        Integer adminId= StpUtil.getLoginIdAsInt();
        String fileId = fileService.uploadNewsCoverFile(file,adminId);
        return SaResult.ok().setData(fileId);
    }

    /**
     * 上传管理员头像
     * @param file 头像文件
     * @return
     * @apiNote 只上传文件不修改数据
     */
    @PostMapping("/upload/avatar")
    @SaCheckLogin
    public SaResult uploadAdminAvatar(@NotNull(message = "封面文件不能为空") @RequestPart MultipartFile file) throws FileUploadException
    {
        Integer adminId= StpUtil.getLoginIdAsInt();
        String fileId = fileService.uploadAdminAvatar(file,adminId);
        return SaResult.ok().setData(fileId);
    }

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


}

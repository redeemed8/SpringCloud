package com.jchhh.media.api;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.media.model.dto.QueryMediaParamsDto;
import com.jchhh.media.model.dto.UploadFileParamsDto;
import com.jchhh.media.model.dto.UploadFileResultDto;
import com.jchhh.media.model.pojo.MediaFiles;
import com.jchhh.media.service.MediaFileService;
import com.jchhh.media.utils.ExtensionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 媒资文件管理接口
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    MediaFileService mediaFileService;

    private static final long _5MB_ = 1048575L * 5;

    @Autowired
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams
            , @RequestBody(required = false) QueryMediaParamsDto queryMediaParamsDto) {
        PageResult<MediaFiles> pageResult = mediaFileService.queryMediaFiles(123456789L, pageParams, queryMediaParamsDto);
        return pageResult;
    }

    @ApiOperation("上传图片接口")
    @RequestMapping(value = "/upload/itemfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto uploadFile(@RequestPart("filedata") MultipartFile filedata) throws IOException {
        if (filedata == null || "".equals(filedata.getOriginalFilename())) {
            return new UploadFileResultDto(false, null, "请上传图片!");
        }
        if (filedata.getSize() > _5MB_) {
            return new UploadFileResultDto(false, null, "图片文件大小不得超过 5MB!");
        }
        //  检查后缀名
        String filename = filedata.getOriginalFilename();
        if (ExtensionUtils.noStandardExtensionWithPhoto(filename)) {
            return new UploadFileResultDto(false, null, "只支持jpg和png格式的图片!");
        }
        //  接收到文件了, 先创建一个临时文件
        File tempFile = File.createTempFile("minio", "temp");
        tempFile.deleteOnExit();
        filedata.transferTo(tempFile);
        //  然后得到它的绝对文件路径
        String localFilePath = tempFile.getAbsolutePath();
        //  临时的 商铺 id
        Long businessId = 123456789L;
        //  准备上传文件的信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());            //  原始文件名称
        uploadFileParamsDto.setFileSize(filedata.getSize());                        //  文件大小
        uploadFileParamsDto.setFileType("图片");
        //  调用service 上传图片
        return mediaFileService.uploadFile(businessId, uploadFileParamsDto, localFilePath);
    }

    @ApiOperation("网络测试接口")
    @GetMapping("/testping")
    public String ping() {
        return "success!";
    }

}

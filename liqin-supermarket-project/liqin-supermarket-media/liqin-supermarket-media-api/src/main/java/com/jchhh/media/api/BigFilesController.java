package com.jchhh.media.api;

import com.jchhh.base.model.RestResponse;
import com.jchhh.media.model.dto.UploadFileParamsDto;
import com.jchhh.media.model.pojo.MediaFiles;
import com.jchhh.media.model.pojo.MediaProcess;
import com.jchhh.media.service.MediaFileService;
import com.jchhh.media.service.MediaProcessService;
import com.jchhh.media.utils.ExtensionUtils;
import com.jchhh.media.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    private MediaFileService mediaFileService;
    private MediaProcessService mediaProcessService;

    @Autowired
    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @Autowired
    public void setMediaProcessService(MediaProcessService mediaProcessService) {
        this.mediaProcessService = mediaProcessService;
    }

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        RestResponse<Boolean> booleanRestResponse = mediaFileService.checkFile(fileMd5);
        return booleanRestResponse;
    }

    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") Integer chunk) {
        RestResponse<Boolean> booleanRestResponse = mediaFileService.checkChunk(fileMd5, chunk);
        return booleanRestResponse;
    }

    @ApiOperation(value = "上传视频")
    @PostMapping("/upload/video")
    public RestResponse<MediaFiles> uploadVideo(@RequestPart("file") MultipartFile file) throws IOException {
        //  如果视频名字为空，则没有上传视频
        if ("".equals(file.getOriginalFilename())) {
            return RestResponse.fail(null, "请上传视频!");
        }
        if (ExtensionUtils.noStandardExtensionWithVideo(file.getOriginalFilename())) {
            return RestResponse.fail(null, "视频格式不支持!");
        }
        //  名字不为空，我们先计算出分块个数
        int chunkSize = 1024 * 1024 * 5;
        int chunkNum = (int) Math.ceil((file.getSize() * 1.0) / chunkSize);
        //  等待实现 断点续传,剩下的交给前端, 这里已经仁至义尽了
        //  缓冲区
        byte[] bytes = new byte[1024];
        File temp = File.createTempFile("minio-1", "temp");
        temp.deleteOnExit();
        file.transferTo(temp);
        //  计算出该分块文件的 md5
        String md5Hex = DigestUtils.md5Hex(Files.newInputStream(temp.toPath()));
        //  读取流
        RandomAccessFile raf_r = new RandomAccessFile(temp, "r");
        //  手动进行分块
        for (int i = 0; i < chunkNum; i++) {
            File tempFile = File.createTempFile("minio" + i, "temp");
            //  写入文件
            RandomAccessFile raf_rw = new RandomAccessFile(tempFile, "rw");
            int len;
            while ((len = raf_r.read(bytes)) != -1) {
                //  写入
                raf_rw.write(bytes, 0, len);
                if (tempFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
            //  根据我们计算出的分块个数调用上传分块文件进行上传
            MultipartFile multipartFile = FileUtils.FileToMultipartFile(tempFile);
            if (multipartFile == null) {
                return RestResponse.fail(null, "上传有误, 已中断!");
            }
            RestResponse<String> stringRestResponse = uploadChunk(multipartFile, md5Hex, i);
            if (!stringRestResponse.getFlag()) {
                return RestResponse.fail(null, stringRestResponse.getMessage());
            }
            //  删除临时文件
            tempFile.deleteOnExit();
        }
        raf_r.close();
        //  然后进行合并即可
        RestResponse<String> stringRestResponse = mergeChunks(md5Hex, file.getOriginalFilename(), chunkNum);
        if (!stringRestResponse.getFlag()) {
            return RestResponse.fail(null, "上传失败!");
        }
        //  查找数据库视频 根据MD5
        MediaFiles mediaFilesByMd5 = mediaFileService.getMediaFilesByMd5(md5Hex);
        if (mediaFilesByMd5 == null) {
            return RestResponse.fail(null, "上传失败!");
        }
        return RestResponse.ok(mediaFilesByMd5, "上传成功!");
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<String> uploadChunk(@RequestParam("file") MultipartFile file, @RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") Integer chunk) throws IOException {
        RestResponse<Boolean> booleanRestResponse = checkFile(fileMd5);
        if (booleanRestResponse.getFlag()) {
            return RestResponse.fail(null, "该分块已经存在!");
        }
        //  接收到文件了, 先创建一个临时文件
        File tempFile = File.createTempFile("minio", "temp");
        tempFile.deleteOnExit();
        file.transferTo(tempFile);
        //  然后得到它的绝对文件路径
        String localFilePath = tempFile.getAbsolutePath();
        RestResponse<String> stringRestResponse = mediaFileService.uploadChunk(fileMd5, chunk, localFilePath);
        return stringRestResponse;
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<String> mergeChunks(@RequestParam("fileMd5") String fileMd5, @RequestParam("fileName") String fileName,
                                            @RequestParam("chunkTotal") Integer chunkTotal) {
        Long businessId = 123456789L;
        //  文件信息对象
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setTags("视频文件");
        uploadFileParamsDto.setFileType("视频");
        RestResponse<String> stringRestResponse = mediaFileService.mergeChunks(businessId, fileMd5, chunkTotal, uploadFileParamsDto);
        return stringRestResponse;
    }

}

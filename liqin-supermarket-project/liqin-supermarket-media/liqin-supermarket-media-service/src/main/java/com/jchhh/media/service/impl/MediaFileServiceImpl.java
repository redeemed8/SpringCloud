package com.jchhh.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.jchhh.base.exception.LiQinException;
import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.base.model.RestResponse;
import com.jchhh.media.mapper.MediaFilesMapper;
import com.jchhh.media.mapper.MediaProcessMapper;
import com.jchhh.media.model.dto.QueryMediaParamsDto;
import com.jchhh.media.model.dto.UploadFileParamsDto;
import com.jchhh.media.model.dto.UploadFileResultDto;
import com.jchhh.media.model.pojo.MediaFiles;
import com.jchhh.media.model.pojo.MediaProcess;
import com.jchhh.media.service.MediaFileService;
import com.jchhh.media.utils.ExtensionUtils;
import com.jchhh.media.utils.FileUtils;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Value("${minio.bucket.files}")
    private String bucket_mediafiles;

    @Value("${minio.bucket.videofiles}")
    private String bucket_video;

    MediaFilesMapper mediaFilesMapper;
    MediaProcessMapper mediaProcessMapper;
    MinioClient minioClient;
    MediaFileService currentProxy;

    @Autowired
    public void setMediaFilesMapper(MediaFilesMapper mediaFilesMapper) {
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Autowired
    public void setMediaProcessMapper(MediaProcessMapper mediaProcessMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
    }

    @Autowired
    public void setMinioClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Autowired
    public void setCurrentProxy(MediaFileService currentProxy) {
        this.currentProxy = currentProxy;
    }

    @Override
    public MediaFiles getMediaFilesByMd5(String md5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(md5);
        return mediaFiles;
    }

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long businessId, PageParams pageParams
            , QueryMediaParamsDto queryMediaParamsDto) {
        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>
                (list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;
    }

    //  可以根据扩展名取出 mimeType
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;     //  通用mimetype，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //  将文件上传到 minio
    @Override
    public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            //  上传文件
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功!bucket{},objectName{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错,bucket{},objectName{},错误信息{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    //  获取文件默认存储目录路径，年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    //  给一个文件，返回其 md5值
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //  添加待处理任务
    private boolean addWaitingTask(MediaFiles mediaFiles) {
        //   获取 文件的mimeType
        String filename = mediaFiles.getFilename();
        String extension = ExtensionUtils.getExtensionFromVideo(filename);
        if (extension == null) {
            log.error("任务添加失败!filename={},err={}", filename, "无文件后缀");
            return false;
        }
        String mimeType = getMimeType(extension);
        MediaProcess mediaProcess = new MediaProcess();
        if (mimeType.equals("video/x-msvideo")) {
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            //  状态---未处理
            mediaProcess.setStatus("未处理");
            mediaProcess.setCreateDate(LocalDateTime.now());
            //  失败次数默认为 0
            mediaProcess.setFailCount(0);
            mediaProcess.setUrl(null);
            //  插入
            int insert = mediaProcessMapper.insert(mediaProcess);
            if (insert != 1) {
                log.error("任务添加失败!filename={},err={}", filename, "插入数据失败");
                return false;
            }
        }
        //  通过 mimeType判断,如果是 avi视频,写入待处理任务
//        if (".avi".equals(extension)) {
//            mediaProcessMapper.insert()
//        }

        return true;
    }

    //  将文件信息添加到文件夹
    @Transactional
    @Override
    public MediaFiles addMediaFilesToDb(Long businessId, String fileMd5,
                                        UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //  将文件信息保存到数据库
        MediaFiles queryMediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (queryMediaFiles == null) {
            //  没有，才插入数据库
            MediaFiles mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            //  文件 id
            mediaFiles.setId(fileMd5);
            //  商铺 id
            mediaFiles.setBusinessId(businessId);
            //  桶
            mediaFiles.setBucket(bucket);
            //  filePath
            mediaFiles.setFilePath(objectName);
            //  fileId
            mediaFiles.setFileId(fileMd5);
            //  url
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            //  上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //  修改时间
            mediaFiles.setChangeDate(LocalDateTime.now());
            //  状态
            mediaFiles.setStatus("1");
            //  审核状态
            mediaFiles.setAuditStatus("通过");
            //  插入数据库
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.debug("向数据库保存文件信息失败,bucket={},objName={}", bucket, objectName);
                return null;
            }
            //  记录待处理任务,将 avi ---> mp4
            if (".avi".equals(ExtensionUtils.getExtensionFromVideo(mediaFiles.getFilename()))) {
                boolean b = addWaitingTask(mediaFiles);
                if (!b) {
                    return null;
                }
            }
            return mediaFiles;
        }
        //  如果根据 md5值查出来的结果存在，说明该文件已经存在一个一模一样的，那我们就更新它的修改时间即可
        queryMediaFiles.setChangeDate(LocalDateTime.now());
        return queryMediaFiles;
    }

    @Override
    public UploadFileResultDto uploadFile(Long businessId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //  先拿到文件名
        String filename = uploadFileParamsDto.getFilename();
        //  再拿到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //  得到 mimeType
        String mimeType = getMimeType(extension);
        //  默认存储路径，当前日期
        String defaultFolderPath = getDefaultFolderPath();
        //   文件的 md5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        //  拼成 objectName
        String objectName = defaultFolderPath + fileMd5 + extension;
        //  将文件上传到 minio
        boolean ret = addMediaFilesToMinio(localFilePath, mimeType, bucket_mediafiles, objectName);
        if (!ret) {
            LiQinException.cast("上传文件失败!");
            return new UploadFileResultDto(false, null, "上传文件失败!");
        }
        //  入库文件信息
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(businessId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        if (mediaFiles == null) {
            LiQinException.cast("文件上传后, 保存信息失败!");
            return new UploadFileResultDto(false, null, "保存文件失败!");
        }
        return new UploadFileResultDto(true, mediaFiles, "上传成功!");
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //   先查询数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            //  数据不存在
            return RestResponse.fail(false, "该数据不存在!");
        }
        //  如果数据库存在，再查 minio
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(mediaFiles.getBucket())
                .object(mediaFiles.getFilePath())
                .build();
        FilterInputStream inputStream;
        try {
            inputStream = minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.fail(false, "数据异常!");
        }
        return inputStream == null ?
                RestResponse.fail(false, "该数据不存在!") : RestResponse.ok(true, "已查到该数据!");
    }

    //  根据 md5得到分块文件的路径
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/chunk/";
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, Integer chunkIndex) {
        //  分块的存储路径是，md5的前两位为子目录，chunk存储分块文件
        //  根据 md5得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);   //  */*/*****/chunk/
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket_video)
                .object(chunkFileFolderPath + chunkIndex)
                .build();
        FilterInputStream inputStream;
        try {
            inputStream = minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.fail(false, "数据异常!");
        }
        return inputStream == null ?
                RestResponse.fail(false, "该数据不存在!") : RestResponse.ok(true, "已查到该数据!");
    }

    @Override
    public RestResponse<String> uploadChunk(String fileMd5, Integer chunk, String localChuckFilePath) {
        //  得到分块文件路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
        //  得到分块文件类型
        String mimeType = getMimeType(null);
        //  将分块文件上传到 minio
        boolean b = addMediaFilesToMinio(localChuckFilePath, mimeType, bucket_video, chunkFilePath);
        if (!b) {
            return RestResponse.fail(null, "上传分块文件信息失败!");
        }
        return RestResponse.ok(fileMd5, "上传分块文件成功!");
    }

    //  根据 md5得到合并文件的路径
    private String getFilePathByMd5(String fileMd5, String fileExtension) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExtension;
    }

    @Override
    public File downloadFileFromMinio(String bucket, String objectName) {
        //  临时 文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //  创建临时文件
            minioFile = File.createTempFile("minio", "merge");
            minioFile.deleteOnExit();
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private boolean clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            Iterable<DeleteObject> objects = Stream.iterate(0, i -> ++i).limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket_video).objects(objects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            //  要想真正的删除, 要遍历一次
            results.forEach(f -> {
                try {
                    DeleteError deleteError = f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public RestResponse<String> mergeChunks(Long businessId, String fileMd5, Integer chunkTotal,
                                            UploadFileParamsDto uploadFileParamsDto) {
        //  分块文件目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //  找到分块文件调用 minio的 sdk进行合并
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(chunkTotal).map(i -> ComposeSource.builder()
                .bucket(bucket_video)
                .object(chunkFileFolderPath + i)
                .build()).collect(Collectors.toList());
        //  拿到 objectname
        String extension = ExtensionUtils.getExtensionFromVideo(uploadFileParamsDto.getFilename());
        if (extension == null) {
            return RestResponse.fail(null, "文件名格式异常!");
        }
        String objectName = getFilePathByMd5(fileMd5, extension);
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(bucket_video)
                .object(objectName)       //   合并后的文件的 objectname
                .sources(sources)
                .build();
        // ================ 合并文件 ================
        try {
            minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("合并文件出错，bucket={},objectName={},错误信息={}", bucket_video, objectName, e.getMessage());
            return RestResponse.fail(null, "合并文件出错!");
        }
        // ================ 校验合并后的和源文件是否一致，一致--视频上传才成功 ================
        //  先下载到本地
        File file = downloadFileFromMinio(bucket_video, objectName);
        String fileMd5_merge = FileUtils.getFileMd5(file);
        if (fileMd5_merge == null) {
            return RestResponse.fail(null, "文件异常,上传中止!");
        }
        //  设置文件大小
        uploadFileParamsDto.setFileSize(file.length());
        if (!fileMd5_merge.equals(fileMd5)) {
            log.error("校验合并文件MD5不一致,原始文件{},合并文件{}", fileMd5, fileMd5_merge);
            return RestResponse.fail(null, "文件异常,上传中止!");
        }
        // ================ 将文件信息入库 ================
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(businessId, fileMd5
                , uploadFileParamsDto, bucket_video, objectName);
        if (mediaFiles == null) {
            return RestResponse.fail(null, "文件入库失败!");
        }
        // ================ 清理分块文件 ================
        boolean b = clearChunkFiles(chunkFileFolderPath, chunkTotal);
        if (!b) {
            return RestResponse.fail(null, "分块文件清理失败");
        }
        return RestResponse.ok(fileMd5, "上传成功!");
    }

}

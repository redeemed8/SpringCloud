package com.jchhh.media.service;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.base.model.RestResponse;
import com.jchhh.media.model.dto.QueryMediaParamsDto;
import com.jchhh.media.model.dto.UploadFileParamsDto;
import com.jchhh.media.model.dto.UploadFileResultDto;
import com.jchhh.media.model.pojo.MediaFiles;

import java.io.File;

/**
 * 媒资文件管理业务类
 */
public interface MediaFileService {

    /**
     * 将文件上传到 minio
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    路径名
     * @return 是否成功
     */
    boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName);

    /**
     * 根据 MD5值查找文件资料
     *
     * @param md5 文件的 md5值
     * @return 查找结果
     */
    MediaFiles getMediaFilesByMd5(String md5);

    /**
     * 媒资文件查询方法
     *
     * @param businessId          商铺代码
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return PageResult<MediaFiles>
     */
    PageResult<MediaFiles> queryMediaFiles(Long businessId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param businessId          商铺代码
     * @param uploadFileParamsDto 上传文件的基本参数
     * @param localFilePath       本地文件路径
     * @return controller需要的结果
     */
    UploadFileResultDto uploadFile(Long businessId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 文件信息入库
     */
    MediaFiles addMediaFilesToDb(Long businessId, String fileMd5,
                                 UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 检查文件是否存在
     *
     * @param fileMd5 文件的 Md5
     * @return 是否存在 RestResponse<Boolean>
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     *
     * @param fileMd5    文件的 Md5
     * @param chunkIndex 分块索引
     * @return 是否存在 RestResponse<Boolean>
     */
    RestResponse<Boolean> checkChunk(String fileMd5, Integer chunkIndex);

    /**
     * 上传分块文件
     *
     * @param fileMd5            该文件的 md5值
     * @param chunk              分块序号
     * @param localChuckFilePath 本地文件暂存路径
     * @return 上传结果
     */
    RestResponse<String> uploadChunk(String fileMd5, Integer chunk, String localChuckFilePath);

    /**
     * 合并文件
     *
     * @param businessId          商铺 id
     * @param fileMd5             文件的 md5值
     * @param chunkTotal          分块总数
     * @param uploadFileParamsDto 上传文件的参数
     * @return 合并结果
     */
    RestResponse<String> mergeChunks(Long businessId, String fileMd5, Integer chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从 minio 下载视频
     *
     * @param bucket     桶
     * @param objectName 路径+名
     * @return 下载的文件
     */
    File downloadFileFromMinio(String bucket, String objectName);

}

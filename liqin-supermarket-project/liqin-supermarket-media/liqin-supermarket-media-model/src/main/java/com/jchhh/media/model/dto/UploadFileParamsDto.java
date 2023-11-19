package com.jchhh.media.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 文件信息
 */
@Data
@ToString
public class UploadFileParamsDto {

    //  文件名
    private String filename;
    //  文件类型
    private String fileType;
    //  文件大小
    private Long fileSize;
    //  标签
    private String tags;
    //  上传人
    private String username;
    //  备注
    private String remark;
}

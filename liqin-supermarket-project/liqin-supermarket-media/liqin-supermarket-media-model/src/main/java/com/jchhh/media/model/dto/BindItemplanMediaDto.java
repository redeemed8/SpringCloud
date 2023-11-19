package com.jchhh.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//  将媒资和商品进行绑定
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "BindItemplanMediaDto", description = "商品-媒资绑定提交数据")
public class BindItemplanMediaDto {

    @ApiModelProperty(value = "媒资文件id", required = true)
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称", required = true)
    private String mediaFileName;

    @ApiModelProperty(value = "商品计划标识", required = true)
    private Long itemplanId;

}

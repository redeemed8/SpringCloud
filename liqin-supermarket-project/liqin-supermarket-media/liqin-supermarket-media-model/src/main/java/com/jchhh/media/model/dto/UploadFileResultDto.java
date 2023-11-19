package com.jchhh.media.model.dto;


import com.jchhh.media.model.pojo.MediaFiles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResultDto {

    private Boolean flag;

    private MediaFiles mediaFiles;

    private String message;

}

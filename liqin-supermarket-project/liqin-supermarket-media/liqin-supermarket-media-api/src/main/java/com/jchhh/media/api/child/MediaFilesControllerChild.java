package com.jchhh.media.api.child;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.media.api.MediaFilesController;
import com.jchhh.media.model.dto.QueryMediaParamsDto;
import com.jchhh.media.model.pojo.MediaFiles;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口2", tags = "媒资文件管理接口2")
@RestController
public class MediaFilesControllerChild {

    private MediaFilesController parent;

    @Autowired
    public void setParent(MediaFilesController parent) {
        this.parent = parent;
    }

    @PostMapping("/files/child")
    public PageResult<MediaFiles> list(Long pageNo, Long pageSize,
                                       String filename, String fileType, String auditStatus) {
        return parent.list(new PageParams(pageNo, pageSize),
                new QueryMediaParamsDto(filename, fileType, auditStatus));
    }

}

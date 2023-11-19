package com.jchhh.media.api.child;

import com.jchhh.base.model.RestResponse;
import com.jchhh.media.api.ItemplanController;
import com.jchhh.media.model.dto.BindItemplanMediaDto;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商品计划编辑接口2", tags = "商品计划编辑接口2")
@RestController
public class ItemplanControllerChild {

    private ItemplanController parent;

    @Autowired
    public void setParent(ItemplanController parent) {
        this.parent = parent;
    }

    @PostMapping("itemplan/association/media/child")
    public RestResponse<String> associationMedia(String mediaId, String mediaFileName, Long itemplanId) {
        return parent.associationMedia(new BindItemplanMediaDto(mediaId, mediaFileName, itemplanId));
    }

}

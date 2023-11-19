package com.jchhh.media.api;

import com.jchhh.base.model.RestResponse;
import com.jchhh.media.model.dto.BindItemplanMediaDto;
import com.jchhh.media.service.ItemplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商品计划编辑接口", tags = "商品计划编辑接口")
@RestController
public class ItemplanController {

    private ItemplanService itemplanService;

    @Autowired
    public void setItemplanService(ItemplanService itemplanService) {
        this.itemplanService = itemplanService;
    }

    @ApiOperation(value = "商品计划和媒资进行绑定")
    @PostMapping("itemplan/association/media")
    public RestResponse<String> associationMedia(@RequestBody BindItemplanMediaDto bindItemplanMediaDto) {
        return itemplanService.association(bindItemplanMediaDto);
    }

}

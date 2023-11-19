package com.jchhh.content.api;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.dto.EditCommonItemDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.service.CommonItemInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "商品信息管理接口", tags = "商品信息管理接口")
@RestController
public class CommonItemInfoController {


    private CommonItemInfoService commonItemInfoService;

    @Autowired
    public void setCommonItemInfoService(CommonItemInfoService commonItemInfoService) {
        this.commonItemInfoService = commonItemInfoService;
    }

    @ApiOperation("商品信息分页查询接口")
    @PostMapping("/item/list")
    public PageResult<CommonItem> list(PageParams pageParams
            , @RequestBody(required = false) QueryItemParamsDto queryItemParamsDto) {
        PageResult<CommonItem> pageResult = commonItemInfoService.queryCommonItemList(pageParams, queryItemParamsDto);
        return pageResult;
    }

    @ApiOperation("根据商品代码查询商品接口")
    @GetMapping("/item/{itemId}")
    public CommonItemInfoDto getCommonItemById(@PathVariable String itemId) {
        return commonItemInfoService.getCommonItemById(itemId);
    }


    @ApiOperation("新建商品接口")
    @PostMapping("/newbuilt/item")
    public CommonItemInfoDto createCommonItem(@RequestBody @Validated AddItemDto addItemDto) {
        return commonItemInfoService.createCommonItem(addItemDto);
    }

    @ApiOperation("修改商品信息接口")
    @PutMapping("/update/item")
    public CommonItemInfoDto modifyCommonItem(@RequestBody EditCommonItemDto editCommonItem) {
        return commonItemInfoService.updateCommonItem(editCommonItem);
    }

    @ApiOperation("删除商品信息接口")
    @DeleteMapping("/delete/item")
    public CommonItemInfoDto deleteCommonItem(String itemId) {
        return commonItemInfoService.deleteCommonItem(itemId);
    }

    @ApiOperation("网络测试接口")
    @GetMapping("/testping")
    public String ping() {
        return "success!";
    }

}

package com.jchhh.content.api;

import com.jchhh.content.model.dto.ItemCategoryTreeDto;
import com.jchhh.content.service.ItemCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "商品分类管理接口", tags = "商品分类管理接口")
@RestController
public class ItemCategoryController {

    private ItemCategoryService itemCategoryService;

    @Autowired
    public void setItemCategoryService(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    @ApiOperation("商品分类查询接口")
    @GetMapping("/item-category/tree-nodes")
    public List<ItemCategoryTreeDto> queryTreeNodes() {
        //   用于页面左侧的展示栏
        return itemCategoryService.queryTreeNodes("1");
    }

}

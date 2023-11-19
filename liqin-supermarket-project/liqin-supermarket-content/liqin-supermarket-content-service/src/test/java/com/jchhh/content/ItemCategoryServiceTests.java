package com.jchhh.content;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.model.dto.ItemCategoryTreeDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.service.CommonItemInfoService;
import com.jchhh.content.service.ItemCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ItemCategoryServiceTests {

    @Autowired
    ItemCategoryService itemCategoryService;

    @Test
    public void testCommonItemMapper1() {
        List<ItemCategoryTreeDto> categoryTreeDtos = itemCategoryService.queryTreeNodes("1-1");
        System.out.println(categoryTreeDtos);
    }


}

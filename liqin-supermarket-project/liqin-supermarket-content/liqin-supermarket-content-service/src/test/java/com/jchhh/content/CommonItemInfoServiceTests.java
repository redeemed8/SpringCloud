package com.jchhh.content;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.service.CommonItemInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommonItemInfoServiceTests {

    @Autowired
    CommonItemInfoService service;

    @Test
    public void testCommonItemMapper1() {
        PageResult<CommonItem> pageResult1 = service.queryCommonItemList(
                new PageParams(1L, 100L), null);
        pageResult1.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper2() {
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setItemName("雪碧");

        PageResult<CommonItem> pageResult1 = service.queryCommonItemList(
                new PageParams(1L, 100L), itemParamsDto);
        pageResult1.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper3() {
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setDiscount(true);

        PageResult<CommonItem> pageResult1 = service.queryCommonItemList(
                new PageParams(1L, 100L), itemParamsDto);
        pageResult1.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper4() {
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setUnitPrice(50);

        PageResult<CommonItem> pageResult1 = service.queryCommonItemList(
                new PageParams(1L, 100L), itemParamsDto);
        pageResult1.getItems().forEach(System.out::println);
    }

    @Test
    public void testCreateCommonItem() {
//        CommonItemInfoDto result = service.createCommonItem(
//                new AddItemDto(null, null, 165, 90, false));
        CommonItemInfoDto result = service.createCommonItem(null);
        System.out.println(result);
    }

}

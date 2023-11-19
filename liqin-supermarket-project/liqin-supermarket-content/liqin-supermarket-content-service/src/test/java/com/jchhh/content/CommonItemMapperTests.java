package com.jchhh.content;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.mapper.CommonItemMapper;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class CommonItemMapperTests {

    @Autowired
    CommonItemMapper commonItemMapper;

    @Test
    public void testCommonItemMapper1() {
        CommonItem commonItem = commonItemMapper.selectById(1);
        System.out.println(commonItem);
        Assertions.assertNotNull(commonItem);
    }

    @Test
    public void testCommonItemMapper2() {
        //  详细进行分页查询的单元测试
        //  查询条件
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setItemName("雪");    //  商品名称查询条件
        //  拼接查询
        LambdaQueryWrapper<CommonItem> queryWrapper = new LambdaQueryWrapper<>();
        //  根据 名称 进行 模糊查询
        queryWrapper.like(StringUtils.isNoneEmpty(itemParamsDto.getItemName())
                , CommonItem::getItemName, itemParamsDto.getItemName());
        //  创建 page分页参数对象
        Page<CommonItem> page = new Page<>(1, 10);
        Page<CommonItem> commonItemPage = commonItemMapper.selectPage(page, queryWrapper);

        PageResult<CommonItem> pageResult = new PageResult<>(commonItemPage.getRecords(), commonItemPage.getSize(),
                1, 10);
        System.out.println(pageResult.getCounts());
        System.out.println(pageResult.getPage());
        System.out.println(pageResult.getPageSize());
        pageResult.getItems().forEach(System.out::println);

    }

    @Test
    public void testCommonItemMapper3() {
        //  详细进行分页查询的单元测试
        //  查询条件
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setDiscount(Boolean.valueOf("0"));
        //  拼接查询
        LambdaQueryWrapper<CommonItem> queryWrapper = new LambdaQueryWrapper<>();
        //  根据 是否优惠 进行 精确查询
        queryWrapper.eq(StringUtils.isNoneEmpty(itemParamsDto.getDiscount().toString())
                , CommonItem::getDiscount, itemParamsDto.getDiscount());

        Page<CommonItem> page = new Page<>(1, 3);
        //  创建 page分页参数对象
        Page<CommonItem> queryPage = commonItemMapper.selectPage(page, queryWrapper);

        PageResult<CommonItem> pageResult = new PageResult<>(queryPage.getRecords(), queryPage.getSize(),
                1, 3);
        System.out.println(pageResult.getCounts());
        System.out.println(pageResult.getPage());
        System.out.println(pageResult.getPageSize());
        pageResult.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper4() {
        QueryItemParamsDto itemParamsDto = new QueryItemParamsDto();
        itemParamsDto.setUnitPrice(100);
        //  拼接查询
        LambdaQueryWrapper<CommonItem> queryWrapper = new LambdaQueryWrapper<>();
        //  根据 单价 进行  范围查询
        queryWrapper.between(StringUtils.isNoneEmpty(itemParamsDto.getUnitPrice() + "")
                , CommonItem::getUnitPrice, 20, 50);
        Page<CommonItem> page = new Page<>(2, 2);
        Page<CommonItem> queryPage = commonItemMapper.selectPage(page, queryWrapper);
        PageResult<CommonItem> pageResult = new PageResult<>(queryPage.getRecords(), queryPage.getSize(), 1, 100);
        System.out.println(pageResult.getCounts());
        System.out.println(pageResult.getPage());
        System.out.println(pageResult.getPageSize());
        pageResult.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper5() {
        //  测试无条件查询
        Page<CommonItem> page = new Page<>(1, 100);
        Page<CommonItem> queryPage = commonItemMapper.selectPage(page, null);
        PageResult<CommonItem> pageResult = new PageResult<>(queryPage.getRecords(), queryPage.getSize(), 1, 100);
        System.out.println(pageResult.getCounts());
        System.out.println(pageResult.getPage());
        System.out.println(pageResult.getPageSize());
        pageResult.getItems().forEach(System.out::println);
    }

    @Test
    public void testCommonItemMapper6() {
        String nowTime = LocalDateTime.now().toString().replaceAll("T", " ").substring(0, 19);
        //  根据id  测试更新数据
        int num = commonItemMapper.updateById(new CommonItem(3L, "103", "伊利纯牛奶", 9, 136,
                nowTime, true));
        System.out.println(num);
    }

    @Test
    public void testCommonItemMapper7() {
        String nowTime = LocalDateTime.now().toString().replaceAll("T", " ").substring(0, 19);
        //  根据id  测试更新数据
        CommonItem commonItem = new CommonItem(3L, "103", "伊利纯牛奶", 9, 136,
                nowTime, true);

        LambdaUpdateWrapper<CommonItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(true, CommonItem::getItemId, "116");

        CommonItem commonItem2 = new CommonItem();
        commonItem2.setUnitPrice(36);
        commonItem2.setLastUpdated(nowTime);
        int num = commonItemMapper.update(commonItem2, updateWrapper);
        System.out.println(num);
    }

    @Test
    public void testCommonItemMapper8() {
        //  也可以使用  LambdaQueryWrapper 作为条件
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", 112);
        int i = commonItemMapper.deleteByMap(map);
        System.out.println(i);
    }

}

package com.jchhh.content;

import com.jchhh.content.mapper.ItemCategoryMapper;
import com.jchhh.content.model.dto.ItemCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ItemCategoryMapperTests {

    @Autowired
    ItemCategoryMapper itemCategoryMapper;

    @Test
    public void testItemCategoryMapper1() {
        List<ItemCategoryTreeDto> list = itemCategoryMapper.selectTreeNodes("1-1");
        System.out.println(list);

    }

}

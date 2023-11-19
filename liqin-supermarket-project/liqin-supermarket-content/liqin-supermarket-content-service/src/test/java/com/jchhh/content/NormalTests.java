package com.jchhh.content;

import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.uitls.AddItemDtoUtils;
import com.jchhh.content.uitls.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class NormalTests {

    @Test
    public void test01() {
        String s = LocalDateTime.now().toString().replaceAll("T", " ").substring(0, 19);
        System.out.println(s.length());
        System.out.println(s);


        StringBuilder stringBuilder = new StringBuilder("123456789");
        System.out.println(stringBuilder.toString().contains("…Ã∆∑1"));

        System.out.println(stringBuilder.substring(0, stringBuilder.length() - 1));
    }

    @Test
    public void test02() {
        AddItemDto www = new AddItemDto("123", "www", 165, 90, false);
        CommonItem commonItem = AddItemDtoUtils.ToCommonItem(www);
        System.out.println(commonItem);
    }

    @Test
    public void testAllFieldsNull() throws IllegalAccessException {
        CommonItem commonItem = new CommonItem();
        System.out.println(ObjectUtils.objFieldAllNull(commonItem));

        CommonItem commonItem1 = new CommonItem();
        commonItem1.setId(1L);
        Boolean aBoolean = ObjectUtils.objFieldAllNull(commonItem1);
        System.out.println(aBoolean);
    }

}

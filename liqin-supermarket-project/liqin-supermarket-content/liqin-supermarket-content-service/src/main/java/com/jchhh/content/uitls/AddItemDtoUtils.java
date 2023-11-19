package com.jchhh.content.uitls;

import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.pojo.CommonItem;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddItemDtoUtils {

    public static CommonItem ToCommonItem(AddItemDto addItemDto) {
        String nowTime = LocalDateTime.now().toString().replaceAll("T", " ").substring(0, 19);
//        return new CommonItem(null,
//                addItemDto.getItemId(),
//                addItemDto.getItemName(),
//                addItemDto.getUnitPrice(),
//                addItemDto.getStock(),
//                nowTime,
//                addItemDto.getDiscount());
        CommonItem commonItem = new CommonItem();
        BeanUtils.copyProperties(addItemDto, commonItem);
        commonItem.setId(null);
        commonItem.setLastUpdated(nowTime);
        return commonItem;
    }

    public static CommonItemInfoDto checkAddItem(AddItemDto addItemDto) {
        if (addItemDto == null) {
            return new CommonItemInfoDto(false, null, "参数不能为空!");
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag;
        CommonItem commonItem;
        String message;
        if (addItemDto.getItemId() == null) {
            stringBuilder.append("商品id,");
        }
        if (addItemDto.getItemName() == null) {
            stringBuilder.append("商品名称,");
        }
        if (addItemDto.getUnitPrice() == null) {
            stringBuilder.append("商品单价,");
        }
        if (addItemDto.getStock() == null) {
            stringBuilder.append("商品库存,");
        }
        if (addItemDto.getDiscount() == null) {
            stringBuilder.append("商品优惠策略,");
        }
        if (stringBuilder.toString().contains("商品")) {
            flag = false;
            commonItem = null;
            message = stringBuilder.substring(0, stringBuilder.length() - 1) + " 不能为空!";
        } else {
            flag = true;
            commonItem = ToCommonItem(addItemDto);
            message = "";
        }
        return new CommonItemInfoDto(flag, commonItem, message);
    }

}

package com.jchhh.content.model.dto;

import com.jchhh.content.model.pojo.CommonItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonItemInfoDto {

    private Boolean flag;

    private CommonItem commonItem;

    private String message;

    public static CommonItemInfoDto ok(CommonItem commonItem1, String msg) {
        return new CommonItemInfoDto(true, commonItem1, msg);
    }

    public static CommonItemInfoDto fail(CommonItem commonItem1, String msg) {
        return new CommonItemInfoDto(false, commonItem1, msg);
    }

}

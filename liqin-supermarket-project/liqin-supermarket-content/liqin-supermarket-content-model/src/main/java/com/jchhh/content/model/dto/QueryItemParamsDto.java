package com.jchhh.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryItemParamsDto {

    //商品名称
    private String itemName;

    //商品单价
    private Integer unitPrice;

    //商品是否优惠
    private Boolean discount;

}

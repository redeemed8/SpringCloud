package com.jchhh.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class EditCommonItemDto {

    /**
     * 0-1. 要修改的商品代码
     */
    @NotEmpty(message = "要修改的商品代码--未填")
    private String toQueryItemId;

    /**
     * 0-2. 要修改的数据体
     */
    private UpdateData updateData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UpdateData {
        /**
         * 1. 商品代码, 到时候让前端给选项即可
         */
        @NotEmpty(message = "商品代码不能为空!")
        private String itemId;

        /**
         * 2. 商品名称
         */
        @NotEmpty(message = "商品名称不能为空!")
        private String itemName;

        /**
         * 3. 商品单价
         */
        @NotEmpty(message = "商品单价不能为空!")
        private Integer unitPrice;

        /**
         * 4. 商品库存
         */
        @NotEmpty(message = "商品库存不能为空!")
        private Integer stock;

        /**
         * 5. 是否优惠
         */
        @NotEmpty(message = "商品优惠策略不能为空!")
        private Boolean discount;
    }

    public EditCommonItemDto(String toQueryItemId, UpdateData updateData) {
        this.toQueryItemId = toQueryItemId;
        this.updateData = updateData;
    }

    public EditCommonItemDto(String toQueryItemId,
                             String itemId, String itemName, Integer unitPrice, Integer stock, Boolean discount) {
        this.toQueryItemId = toQueryItemId;
        this.updateData = new UpdateData(itemId, itemName, unitPrice, stock, discount);
    }

}

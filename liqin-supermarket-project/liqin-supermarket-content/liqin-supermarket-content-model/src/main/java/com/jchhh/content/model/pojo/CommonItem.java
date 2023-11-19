package com.jchhh.content.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("common_item")
public class CommonItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 1. 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 2. 商品代码
     */
    private String itemId;

    /**
     * 3. 商品名称
     */
    private String itemName;

    /**
     * 4. 商品单价
     */
    private Integer unitPrice;

    /**
     * 5. 商品库存
     */
    private Integer stock;

    /**
     * 6. 商品信息最近更新时间
     */
    private String lastUpdated;

    /**
     * 7. 是否优惠
     */
    private Boolean discount;


}

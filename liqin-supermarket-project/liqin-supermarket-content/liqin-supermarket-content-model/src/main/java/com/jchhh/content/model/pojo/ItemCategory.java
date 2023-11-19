package com.jchhh.content.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("item_category")
public class ItemCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，当前节点号
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类标签
     */
    private String label;

    /**
     * 父节点号
     */
    private String parentId;

    /**
     * 是否可以进行展示
     */
    private Boolean allowShow;

    /**
     * 所在层级的序号
     */
    private Integer orderby;

    /**
     * 是否是叶子节点
     */
    private Boolean beLeaf;

}

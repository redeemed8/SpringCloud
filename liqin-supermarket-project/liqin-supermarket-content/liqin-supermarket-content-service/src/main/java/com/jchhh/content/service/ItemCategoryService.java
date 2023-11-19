package com.jchhh.content.service;

import com.jchhh.content.model.dto.ItemCategoryTreeDto;

import java.util.List;

/**
 * 商品分类管理接口
 */
public interface ItemCategoryService {

    /**
     * 分类查询
     *
     * @param id 节点号
     * @return 查询结果
     */
    List<ItemCategoryTreeDto> queryTreeNodes(String id);

}

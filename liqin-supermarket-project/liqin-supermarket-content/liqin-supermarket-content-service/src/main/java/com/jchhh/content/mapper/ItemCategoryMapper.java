package com.jchhh.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jchhh.content.model.dto.ItemCategoryTreeDto;
import com.jchhh.content.model.pojo.ItemCategory;

import java.util.List;

public interface ItemCategoryMapper extends BaseMapper<ItemCategory> {

    //  使用递归查询分类
    List<ItemCategoryTreeDto> selectTreeNodes(String id);


}

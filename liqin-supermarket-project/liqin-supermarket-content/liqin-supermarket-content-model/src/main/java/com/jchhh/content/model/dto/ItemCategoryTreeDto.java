package com.jchhh.content.model.dto;

import com.jchhh.content.model.pojo.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCategoryTreeDto extends ItemCategory implements Serializable {

    //  子节点
    List<ItemCategoryTreeDto> childrenTreeNodes;

}

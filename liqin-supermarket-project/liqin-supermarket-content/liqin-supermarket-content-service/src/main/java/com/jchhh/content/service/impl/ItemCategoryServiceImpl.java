package com.jchhh.content.service.impl;

import com.jchhh.content.mapper.ItemCategoryMapper;
import com.jchhh.content.model.dto.ItemCategoryTreeDto;
import com.jchhh.content.model.pojo.ItemCategory;
import com.jchhh.content.service.ItemCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private ItemCategoryMapper itemCategoryMapper;

    @Autowired
    public void setItemCategoryMapper(ItemCategoryMapper itemCategoryMapper) {
        this.itemCategoryMapper = itemCategoryMapper;
    }

    @Override
    public List<ItemCategoryTreeDto> queryTreeNodes(String id) {
        if (id == null) {
            return new ArrayList<>(0);
        }
        //  调用 mapper递归查询出分类信息
        List<ItemCategoryTreeDto> itemCategoryTreeDtos = itemCategoryMapper.selectTreeNodes(id);
        //  找到每个节点的子节点，最终封装成 List<ItemCategoryTreeDto>
        //  先将 list转换成 map, key就是节点的 id, value就是 ItemCategoryTreeDto对象,目的就是方便从 map中获取节点
        Map<String, ItemCategoryTreeDto> map = itemCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))       //  排除根节点
                .collect(Collectors.toMap(ItemCategory::getId, value -> value, (key1, key2) -> key2));
        //  定义一个 list作为最终返回的 list
        List<ItemCategoryTreeDto> itemCategoryList = new ArrayList<>();
        //  从头遍历 List<ItemCategoryTreeDto> , 一边遍历一边找子节点放在父节点的childrenTreeNodes
        itemCategoryTreeDtos.stream().forEach(item -> {
            //  开始处理,向 list中写入元素
            if (item.getParentId().equals(id)) {
                itemCategoryList.add(item);
            }
            //  找到该节点的父节点
            ItemCategoryTreeDto itemCategoryTreeDto = map.get(item.getParentId());
            if (itemCategoryTreeDto != null) {
                if (itemCategoryTreeDto.getChildrenTreeNodes() == null) {
                    itemCategoryTreeDto.setChildrenTreeNodes(new ArrayList<>());
                }
                //  找到每个节点的子节点放在 childrenTreeNodes中
                itemCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });

        return itemCategoryList;
    }

}

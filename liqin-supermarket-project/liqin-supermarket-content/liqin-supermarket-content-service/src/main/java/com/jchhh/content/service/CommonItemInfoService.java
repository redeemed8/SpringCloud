package com.jchhh.content.service;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.dto.EditCommonItemDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;

/**
 * 商品信息管理接口
 */
public interface CommonItemInfoService {

    /**
     * 商品信息分页查询
     *
     * @param pageParams    分页查询参数
     * @param itemParamsDto 分页查询条件
     * @return 查询结果
     */
    PageResult<CommonItem> queryCommonItemList(PageParams pageParams, QueryItemParamsDto itemParamsDto);

    /**
     * 根据 商品代码 查询商品信息
     *
     * @param itemId 商品代码
     * @return 完整商品信息及查询结果
     */
    CommonItemInfoDto getCommonItemById(String itemId);

    /**
     * 新建商品
     *
     * @param addItemDto 商品部分数据
     * @return 完整商品信息及新建结果
     */
    CommonItemInfoDto createCommonItem(AddItemDto addItemDto);

    /**
     * 根据商品代码 修改商品信息
     *
     * @param editCommonItem 要修改成的信息，必须包括商品代码
     * @return 修改结果
     */
    CommonItemInfoDto updateCommonItem(EditCommonItemDto editCommonItem);

    /**
     * 根据商品代码 删除商品信息
     *
     * @param itemId 要删除的商品代码
     * @return 删除结果
     */
    CommonItemInfoDto deleteCommonItem(String itemId);

}

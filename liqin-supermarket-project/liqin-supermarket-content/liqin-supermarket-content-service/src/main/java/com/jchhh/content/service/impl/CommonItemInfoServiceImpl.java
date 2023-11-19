package com.jchhh.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.mapper.CommonItemMapper;
import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.dto.EditCommonItemDto;
import com.jchhh.content.model.dto.EditCommonItemDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.service.CommonItemInfoService;
import com.jchhh.content.uitls.AddItemDtoUtils;
import com.jchhh.content.uitls.ObjectUtils;
import com.jchhh.content.uitls.PageParamsUtils;
import com.jchhh.content.uitls.PriceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CommonItemInfoServiceImpl
        extends ServiceImpl<CommonItemMapper, CommonItem> implements CommonItemInfoService {

    private CommonItemMapper commonItemMapper;

    @Autowired
    public void setCommonItemMapper(CommonItemMapper commonItemMapper) {
        this.commonItemMapper = commonItemMapper;
    }


    //  商品分页查询
    @Override
    public PageResult<CommonItem> queryCommonItemList(PageParams pageParams, QueryItemParamsDto itemParamsDto) {
        PageParamsUtils.standardizePageParams(pageParams);
        LambdaQueryWrapper<CommonItem> queryWrapper = new LambdaQueryWrapper<>();
        if (itemParamsDto != null) {
            //  拼接查询条件
            //  根据商品名字的模糊查询
            queryWrapper.like(StringUtils.isNoneEmpty(itemParamsDto.getItemName()),
                    CommonItem::getItemName, itemParamsDto.getItemName());
            //  根据商品单价进行范围查询，我们这里采取 查询 单价+-50的范围 如 200 --> (100,300)
            if (itemParamsDto.getUnitPrice() != null) {
                int unitPrice = itemParamsDto.getUnitPrice();
                Map<String, Integer> priceMap = PriceUtils.getMaxAndMinPrice(unitPrice);
                queryWrapper.between(StringUtils.isNoneEmpty(itemParamsDto.getUnitPrice() + ""),
                        CommonItem::getUnitPrice, priceMap.get(PriceUtils.MIN), priceMap.get(PriceUtils.MAX));
            }
            //  根据商品是否有优惠进行精确查询
            queryWrapper.eq(itemParamsDto.getDiscount() != null,
                    CommonItem::getDiscount, itemParamsDto.getDiscount());
        }
        //  设置分页参数
        Page<CommonItem> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CommonItem> queryPage = commonItemMapper.selectPage(page, queryWrapper);
        if (queryPage == null) {
            return new PageResult<>(Collections.emptyList(), 0, 0, 0);
        }
        return new PageResult<>(queryPage.getRecords(), queryPage.getSize(),
                pageParams.getPageNo(), pageParams.getPageSize());
    }


    //  根据商品代码查询商品信息
    @Override
    public CommonItemInfoDto getCommonItemById(String itemId) {
        //  检验参数
        if (itemId == null) {
            return new CommonItemInfoDto(false, null, "商品代码不能为空!");
        }
        //  根据商品代码进行查询
        CommonItem queryItem = query().eq("item_id", itemId).one();
        if (queryItem == null) {
            return new CommonItemInfoDto(false, null, "该商品信息不存在!");
        }
        return new CommonItemInfoDto(true, queryItem, "查询成功!");
    }


    //  新建商品
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonItemInfoDto createCommonItem(AddItemDto addItemDto) {
        //  验证参数是否合法，即每个属性都不为空 !
        CommonItemInfoDto commonItemInfoDto = AddItemDtoUtils.checkAddItem(addItemDto);
        //  如果有错误，直接返回含有错误信息的结果
        if (!commonItemInfoDto.getFlag()) {
            return commonItemInfoDto;
        }

        //  写入数据库之前，要先检查数据库中是否已经存在该商品，用商品的 itemId查询即可, 存在则不添加并返回提示信息
        CommonItem queryItem = query().eq("item_id", addItemDto.getItemId()).one();
        if (queryItem != null && queryItem.getItemId().equals(addItemDto.getItemId())) {
            commonItemInfoDto.setFlag(false);
            commonItemInfoDto.setCommonItem(null);
            commonItemInfoDto.setMessage("该商品id已经存在, 不能重复添加!");
            return commonItemInfoDto;
        }

        //  如果正确, 尝试将基本信息数据写入数据库
        int insert = commonItemMapper.insert(commonItemInfoDto.getCommonItem());
        //  如果写入失败，返回错误信息
        if (insert <= 0) {
            return new CommonItemInfoDto(false, null, "服务器ERROR: 新建商品失败!");
        }
        commonItemInfoDto.setMessage("商品新建成功!");
        return commonItemInfoDto;
    }

    //  修改商品
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonItemInfoDto updateCommonItem(EditCommonItemDto editCommonItemDto) {
        //  检验参数
        if (editCommonItemDto == null || editCommonItemDto.getToQueryItemId() == null) {
            return new CommonItemInfoDto(false, null, "要修改的商品代码不能为空!");
        }
        if (ObjectUtils.objFieldAllNull(editCommonItemDto.getUpdateData())) {
            return new CommonItemInfoDto(false, null, "至少有一项被修改!");
        }
        //  封装数据
        CommonItem commonItem = new CommonItem();
        BeanUtils.copyProperties(editCommonItemDto.getUpdateData(), commonItem);
        commonItem.setId(null);
        commonItem.setLastUpdated(LocalDateTime.now().toString().replaceAll("T", " ").substring(0, 19));
        //  设置更新方式---根据 itemId列的值 确定修改哪一行
        LambdaUpdateWrapper<CommonItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(editCommonItemDto.getToQueryItemId() != null,
                CommonItem::getItemId, editCommonItemDto.getToQueryItemId());
        //  更新数据库
        int update = commonItemMapper.update(commonItem, updateWrapper);
        if (update <= 0) {
            return new CommonItemInfoDto(false, null, "该商品代码无效!");
        }
        if (update > 1) {
            log.error("服务器异常: 数据更新失败!" + commonItem);
            log.error("updateResult = " + update);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new CommonItemInfoDto(false, null, "服务器异常: 数据更新失败!");
        }
        CommonItem resultItem = query().eq("item_id", editCommonItemDto.getToQueryItemId()).one();
        return new CommonItemInfoDto(true, resultItem, "修改成功!");
    }

    @Transactional
    @Override
    public CommonItemInfoDto deleteCommonItem(String itemId) {
        if (itemId == null) {
            return CommonItemInfoDto.fail(null, "请填写商品代码!");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", itemId);
        int deleteResult = commonItemMapper.deleteByMap(map);
        if (deleteResult == 0) {
            return CommonItemInfoDto.fail(null, "商品代码不存在!");
        }
        if (deleteResult != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CommonItemInfoDto.fail(null, "有异常, 删除失败!");
        }
        return CommonItemInfoDto.ok(null, "删除成功!");
    }

}

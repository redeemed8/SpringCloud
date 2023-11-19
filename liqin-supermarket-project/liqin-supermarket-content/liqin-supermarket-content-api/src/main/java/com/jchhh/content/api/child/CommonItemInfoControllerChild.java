package com.jchhh.content.api.child;

import com.jchhh.base.model.PageParams;
import com.jchhh.base.model.PageResult;
import com.jchhh.content.api.CommonItemInfoController;
import com.jchhh.content.model.dto.AddItemDto;
import com.jchhh.content.model.dto.CommonItemInfoDto;
import com.jchhh.content.model.dto.EditCommonItemDto;
import com.jchhh.content.model.dto.QueryItemParamsDto;
import com.jchhh.content.model.pojo.CommonItem;
import com.jchhh.content.uitls.MyBooleanUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonItemInfoControllerChild {

    @Autowired
    private CommonItemInfoController parent;

    @PostMapping("/item/list/child")
    public PageResult<CommonItem> list(Long pageNo, Long pageSize, String itemName, Integer unitPrice, Integer discount) {
        return parent.list(new PageParams(pageNo, pageSize),
                new QueryItemParamsDto(itemName, unitPrice, MyBooleanUtils.toBoolean(discount)));
    }

    @PostMapping("/newbuilt/item/child")
    public CommonItemInfoDto createCommonItem(String itemId, String itemName
            , Integer unitPrice, Integer stock, Integer discount) {
        return parent.createCommonItem(new AddItemDto(itemId, itemName, unitPrice, stock, MyBooleanUtils.toBoolean(discount)));
    }

    @PutMapping("/update/item/child")
    public CommonItemInfoDto modifyCommonItem(String toQueryItemId,
                                              String itemId, String itemName, Integer unitPrice,
                                              Integer stock, Integer discount) {
        itemId = "".equals(itemId) ? null : itemId;
        itemName = "".equals(itemName) ? null : itemName;
        return parent.modifyCommonItem(new EditCommonItemDto(toQueryItemId,
                itemId, itemName, unitPrice, stock, MyBooleanUtils.toBoolean(discount)));
    }

}

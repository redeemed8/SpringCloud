package com.jchhh.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jchhh.base.model.RestResponse;
import com.jchhh.media.mapper.ItemplanMediaMapper;
import com.jchhh.media.mapper.MediaFilesMapper;
import com.jchhh.media.model.dto.BindItemplanMediaDto;
import com.jchhh.media.model.pojo.ItemplanMedia;
import com.jchhh.media.model.pojo.MediaFiles;
import com.jchhh.media.service.ItemplanService;
import com.jchhh.media.service.MediaFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ItemplanServiceImpl implements ItemplanService {

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private ItemplanMediaMapper itemplanMediaMapper;

    @Transactional
    @Override
    public RestResponse<String> association(BindItemplanMediaDto bindItemplanMediaDto) {
        //  判断是否存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(bindItemplanMediaDto.getMediaId());
        if (mediaFiles == null) {
            RestResponse.fail(null, "文件不存在,请先上传");
        }
        //  先删除原有记录, 根据商品计划 id删除它所绑定的媒资
        int delete = itemplanMediaMapper.delete(
                new LambdaQueryWrapper<ItemplanMedia>().eq(ItemplanMedia::getItemplanId, bindItemplanMediaDto.getItemplanId()));
        //  再添加新记录
        ItemplanMedia itemplanMedia = new ItemplanMedia();
        BeanUtils.copyProperties(bindItemplanMediaDto, itemplanMedia);
        itemplanMedia.setBusinessId(123456789L);
        itemplanMedia.setCreateDate(LocalDateTime.now());
        itemplanMedia.setCreatePeople("jchhh");
        itemplanMedia.setChangePeople("jchhh");
        int insert = itemplanMediaMapper.insert(itemplanMedia);
        if (insert == 1) {
            return RestResponse.ok("~" + bindItemplanMediaDto.getMediaFileName(), "绑定成功!");
        }
        return RestResponse.fail(null, "绑定失败!");
    }

}

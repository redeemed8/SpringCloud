package com.jchhh.media.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("itemplan_media")
public class ItemplanMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 1. 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String mediaId;

    private Long itemplanId;

    private Long businessId;

    private String mediaFileName;

    private LocalDateTime createDate;

    private String createPeople;

    private String changePeople;

}

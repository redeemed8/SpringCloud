package com.jchhh.media.service;

import com.jchhh.base.model.RestResponse;
import com.jchhh.media.model.dto.BindItemplanMediaDto;

public interface ItemplanService {

    /**
     * 绑定商品和媒资
     *
     * @param bindItemplanMediaDto bindItemplanMediaDto
     */
    RestResponse<String> association(BindItemplanMediaDto bindItemplanMediaDto);

}

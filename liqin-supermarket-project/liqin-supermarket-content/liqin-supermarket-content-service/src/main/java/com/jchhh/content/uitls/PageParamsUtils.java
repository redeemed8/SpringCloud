package com.jchhh.content.uitls;

import com.jchhh.base.model.PageParams;

public class PageParamsUtils {

    /**
     * 保证分页参数的合理性,页码 > 0 , 容量 >= 0
     *
     * @param pageParams 分页参数
     */
    public static void standardizePageParams(PageParams pageParams) {
        long pageNo = pageParams.getPageNo();
        long pageSize = pageParams.getPageSize();
        if (pageNo <= 0L || pageSize < 0L) {
            pageParams.setPageNo(0L);
            pageParams.setPageSize(0L);
        }
    }

}

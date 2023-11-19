package com.jchhh.order.mapper;

import com.jchhh.order.pojo.Order;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper {
    @Select("select * from tb_order where id = #{id}") Order findById(Long id);
}

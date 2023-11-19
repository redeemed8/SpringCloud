package com.jchhh.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jchhh.media.model.pojo.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Mapper 接口
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    //   1 未处理   2 处理成功   3 处理失败   4 处理中

    @Select("select * from media_process t where t.id % #{shardTotal} = #{shardIndex} and (t.status = '未处理' or t.status = '处理失败') and t.fail_count < 3 limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex,
                                              @Param("count") int count);

    //  开启一个任务
    @Update("update media_process m set m.status = '处理中' where (m.status = '未处理' or m.status = '处理失败') and m.fail_count < 3 and m.id = #{id}")
    int startTask(@Param("id") long id);

}

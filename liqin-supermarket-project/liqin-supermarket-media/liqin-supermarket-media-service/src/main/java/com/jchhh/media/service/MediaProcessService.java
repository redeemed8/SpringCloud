package com.jchhh.media.service;

import com.jchhh.media.model.pojo.MediaProcess;

import java.util.List;

public interface MediaProcessService {

    /**
     * 添加任务到 media_process
     *
     * @param mediaProcess 上传参数
     * @return int
     */
    int insertTask(MediaProcess mediaProcess);

    /**
     * 获取待处理任务
     *
     * @param shardTotal 处理器总数
     * @param shardIndex 处理器序号
     * @param count      获取总记录数
     * @return List<MediaProcess>
     */
    List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务 id
     * @return boolean
     */
    boolean startTask(long id);

    /**
     * 保存任务结果
     *
     * @param taskId   任务 id
     * @param status   结束任务状态码
     * @param fileId   文件
     * @param url      文件保存路径
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);

}

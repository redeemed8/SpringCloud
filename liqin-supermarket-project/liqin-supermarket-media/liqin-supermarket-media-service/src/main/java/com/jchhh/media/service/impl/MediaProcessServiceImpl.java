package com.jchhh.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jchhh.media.context.TaskDealContexts;
import com.jchhh.media.mapper.MediaFilesMapper;
import com.jchhh.media.mapper.MediaProcessHistoryMapper;
import com.jchhh.media.mapper.MediaProcessMapper;
import com.jchhh.media.model.pojo.MediaFiles;
import com.jchhh.media.model.pojo.MediaProcess;
import com.jchhh.media.model.pojo.MediaProcessHistory;
import com.jchhh.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    private MediaProcessMapper mediaProcessMapper;
    private MediaFilesMapper mediaFilesMapper;
    private MediaProcessHistoryMapper processHistoryMapper;

    @Autowired
    public void setMediaProcessMapper(MediaProcessMapper mediaProcessMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
    }

    @Autowired
    public void setMediaFilesMapper(MediaFilesMapper mediaFilesMapper) {
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Autowired
    public void setProcessHistoryMapper(MediaProcessHistoryMapper processHistoryMapper) {
        this.processHistoryMapper = processHistoryMapper;
    }

    @Override
    public int insertTask(MediaProcess mediaProcess) {
        return mediaProcessMapper.insert(mediaProcess);
    }

    @Override
    public List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result > 0;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //  要更新的任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return;
        }
        //  ============= 如果任务执行失败 =============
        if (status.equals(TaskDealContexts.TASK_DEAL_FAIL)) {
            //  更新 media_process表的状态
            mediaProcess.setStatus(TaskDealContexts.TASK_DEAL_FAIL);
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            return;
        }
        //  ============= 如果任务执行成功 =============
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        //  更新 media_file 表中的 url
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        //  更新 media_process 表的状态
        mediaProcess.setStatus(TaskDealContexts.TASK_DEAL_SUCCESS);
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);
        //  将 media_process 表的记录插入到 media_process_history中
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        processHistoryMapper.insert(mediaProcessHistory);
        //  从 media_process 删除当前任务
        mediaProcessMapper.deleteById(taskId);
    }

}

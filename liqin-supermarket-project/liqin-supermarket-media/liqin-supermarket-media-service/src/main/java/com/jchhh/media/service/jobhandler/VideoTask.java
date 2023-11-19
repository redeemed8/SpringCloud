package com.jchhh.media.service.jobhandler;

import com.jchhh.base.utils.Mp4VideoUtil;
import com.jchhh.media.context.TaskDealContexts;
import com.jchhh.media.model.pojo.MediaProcess;
import com.jchhh.media.service.MediaFileService;
import com.jchhh.media.service.MediaProcessService;
import com.jchhh.media.utils.ExtensionUtils;
import com.jchhh.media.utils.FileUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 视频任务处理类
 */
@Slf4j
@Component
public class VideoTask {

    @Autowired
    private MediaProcessService mediaProcessService;

    @Autowired
    private MediaFileService mediaFileService;

    //  ffmpeg的路径
    @Value("${videoprocess.ffmpeg_path}")
    private String ffmpegPath;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();      //  执行器的序号从0开始
        int shardTotal = XxlJobHelper.getShardTotal();      //  执行器总数
        //  确定 cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //  ========= 查询待处理的任务 =========
        List<MediaProcess> mediaProcessList = mediaProcessService.selectListByShardIndex(shardTotal, shardIndex, processors);
        //  任务数量
        int size = mediaProcessList.size();
        //  创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //  使用一个计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            //  将任务加入线程池
            executorService.execute(() -> {
                try {
                    //  任务执行逻辑
                    //  任务 id
                    Long taskId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();  //  文件 Id就是 md5值
                    //  ========= 开启任务 =========
                    boolean b = mediaProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败,任务 id={}", taskId);
                        return;
                    }
                    //  下载 minio视频到本地
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    File file = mediaFileService.downloadFileFromMinio(bucket, objectName);
                    if (file == null) {
                        log.error("下载视频出错,bucket={},objectName={}", bucket, objectName);
                        //  保存任务失败结果
                        mediaProcessService.saveProcessFinishStatus(taskId, TaskDealContexts.TASK_DEAL_FAIL,
                                fileId, null, "下载视频到本地失败");
                        return;
                    }
                    //  源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称

                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    //  先创建一个临时文件，作为转换后的文件
                    File mp4file = null;
                    try {
                        mp4file = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.error("创建临时文件异常,errorMsg={}", e.getMessage());
                        mediaProcessService.saveProcessFinishStatus(taskId, TaskDealContexts.TASK_DEAL_FAIL,
                                fileId, null, "创建临时文件异常");
                        return;
                    }
                    String mp4_path = mp4file.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, video_path, mp4_name, mp4_path);
                    //  ========= 执行视频转码 =========
                    //开始视频转换，成功将返回success, 失败返回失败原因
                    String result = videoUtil.generateMp4();
                    if (!"success".equals(result)) {
                        log.debug("视频转码失败,原因={}", result);
                        mediaProcessService.saveProcessFinishStatus(taskId, TaskDealContexts.TASK_DEAL_FAIL,
                                fileId, null, result);
                        return;
                    }
                    //  ========= 上传到 minio =========
                    //  修改 objectName 的后缀
                    String mp4Obj = ExtensionUtils.modifyFileExtension(objectName, ".mp4");
                    boolean b1 = mediaFileService.addMediaFilesToMinio(mp4file.getAbsolutePath()
                            , "video/mp4", bucket, mp4Obj);
                    if (!b1) {
                        log.debug("上传文件信息到minio失败taskId={},bucket={},objectName={}", taskId, bucket, mp4Obj);
                        mediaProcessService.saveProcessFinishStatus(taskId, TaskDealContexts.TASK_DEAL_FAIL,
                                fileId, null, "上传文件信息到minio失败");
                        return;
                    }
                    //  mp4 文件 url
                    String url = FileUtils.getFilePathByMd5(fileId, ".mp4");
                    //  ========= 保存任务成功处理结果 =========
                    mediaProcessService.saveProcessFinishStatus(taskId, "处理成功", fileId, url, null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        //  阻塞, 指定一个最大限度的等待时间, 防止因为各种原因程序中断
        try {
            countDownLatch.await(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

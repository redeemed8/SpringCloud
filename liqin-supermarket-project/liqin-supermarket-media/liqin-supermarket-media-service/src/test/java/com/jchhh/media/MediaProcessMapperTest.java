package com.jchhh.media;

import com.jchhh.media.mapper.MediaProcessMapper;
import com.jchhh.media.model.pojo.MediaProcess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MediaProcessMapperTest {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Test
    public void test01() {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(1, 0, 10);
        mediaProcesses.forEach(System.out::println);
    }

}

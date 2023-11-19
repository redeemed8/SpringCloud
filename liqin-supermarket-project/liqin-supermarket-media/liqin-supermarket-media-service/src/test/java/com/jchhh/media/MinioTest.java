package com.jchhh.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 测试 minio的 sdk
 */
public class MinioTest {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.110.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void test_upload() throws Exception {

        //  可以根据扩展名取出 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".png");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;     //  通用mimetype，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        //  上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")       //  确定桶
                .filename("D:\\soft\\photo\\zhongduan.png")             //  指定本地文件路径
                .object("zhongduan1.png")     //  对象名
                .contentType(mimeType)
                .build();
        //  上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    @Test
    public void test_delete() throws Exception {
        //  RemoveObjectArgs
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("11.jpg")
                .build();
        //  删除文件
        minioClient.removeObject(removeObjectArgs);
    }

    @Test
    public void test_delete2() throws Exception {

        String bucket = "testbucket";
        String FolderPath = "chunk/";
        int chunkSize = 2;

        for (int integer = 0; integer < chunkSize; integer++) {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(FolderPath + integer)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        }
    }


    @Test
    //  查询文件，从 minio下载
    public void test_getFile() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("zhongduan.png")
                .build();
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        //  指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\soft\\minio\\download\\zhongduan.png"));
        IOUtils.copy(inputStream, outputStream);
        //  检验文件的完整性
        //  对文件值进行 md5即可
        String source_md5 = DigestUtils.md5Hex(
                Files.newInputStream(new File("D:\\soft\\photo\\zhongduan.png").toPath()));
        //  本地文件的 md5
        String local_md5 = DigestUtils.md5Hex(
                Files.newInputStream(new File("D:\\soft\\minio\\download\\zhongduan.png").toPath()));
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功!");
        } else {
            System.out.println("下载有误!");
        }
    }

    //  上传分块文件
    @Test
    public void uploadChunk() throws Exception {
        for (int i = 0; i < 2; i++) {
            //  上传文件的参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")       //  确定桶
                    .filename("D:\\soft\\video\\Chunked\\" + i)             //  指定本地文件路径
                    .object("chunk/" + i)     //  对象名
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传分块 " + i + " 成功!");
        }
    }

    //  调用 minio接口合并分块
    @Test
    public void testMerge() throws Exception {
//        List<ComposeSource> sources = new ArrayList<>();
//
//        //  指定分块文件的信息
//        for (int i = 0; i < 6; i++) {
//            ComposeSource composeSource = ComposeSource.builder()
//                    .bucket("testbucket")
//                    .object("chunk/" + i)
//                    .build();
//            sources.add(composeSource);
//        }

        //  上述代码可以简化
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(2).map(i -> ComposeSource.builder()
                .bucket("testbucket")
                .object("chunk/" + i)
                .build()).collect(Collectors.toList());

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge/01.mp4")
                .sources(sources)
                .build();
        //  合并文件
        minioClient.composeObject(composeObjectArgs);
    }


}

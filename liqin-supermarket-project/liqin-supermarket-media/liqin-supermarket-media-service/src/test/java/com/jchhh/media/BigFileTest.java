package com.jchhh.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 测试大文件上传的方法 --- 分块和合并
 */
public class BigFileTest {

    //  分块测试
    @Test
    public void testChunk() throws IOException {
        //  源文件
        File sourceFile = new File("D:\\soft\\video\\dog.mp4");
        //  分块文件的存储路径
        String chunkFilePath = "D:\\soft\\video\\Chunked\\";
        //  设置分块文件的大小
        int chunkSize = 1024 * 1024 * 5;        //  5 MB
        //  分块文件的个数 --- 总文件大小/分块文件大小 向上取整
        int chunkNum = (int) Math.ceil((sourceFile.length() * 1.0) / chunkSize);
        //  使用流从源文件读数据，向分块文件中写数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        //  给一个缓冲区
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            //  分块文件的写入流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                //  写入
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    //  将分块进行合并
    @Test
    public void testMerge() throws IOException {
        //  块文件路径
        File chunkFolder = new File("D:\\soft\\video\\Chunked\\");
        //  源文件，用来比较 md5值
        File sourceFile = new File("D:\\soft\\video\\dog.mp4");
        //  合并后的文件
        File mergeFile = new File("D:\\soft\\video\\merge\\dog_merge.mp4");

        //  取出所有的分块文件
        File[] files = chunkFolder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("没有分块文件!");
            return;
        }
        //  对取出的文件进行排序
        List<File> filesList = Arrays.asList(files);
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //  向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");

        //  缓冲区
        byte[] bytes = new byte[1024];
        //  遍历分块文件，向合并的文件写
        for (File file : filesList) {
            //  读分块的流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();

        //  看看 MD5值
        String mergeMD5 = DigestUtils.md5Hex(Files.newInputStream(mergeFile.toPath()));
        String srcMD5 = DigestUtils.md5Hex(Files.newInputStream(sourceFile.toPath()));
        if (srcMD5.equals(mergeMD5)) {
            System.out.println("合并成功!");
        } else {
            System.out.println("合并失败!!!!!!!!!!!!!");
        }
    }

}

package com.jchhh.media.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;


public class FileUtils {

    public static MultipartFile FileToMultipartFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            return multipartFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileMd5(File file) {
        if (file == null) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String md5 = DigestUtils.md5Hex(fileInputStream);
            return md5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFilePathByMd5(String fileMd5, String fileExtension) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExtension;
    }

}

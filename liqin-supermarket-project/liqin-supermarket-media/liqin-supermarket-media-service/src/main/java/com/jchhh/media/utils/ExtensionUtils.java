package com.jchhh.media.utils;

public class ExtensionUtils {

    public static String getExtensionFromVideo(String filename) {
        if (noStandardExtensionWithVideo(filename)) {
            return null;
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public static String getExtensionFromPhoto(String filename) {
        if (noStandardExtensionWithPhoto(filename)) {
            return null;
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    //  检查是否不合格
    public static boolean noStandardExtensionWithPhoto(String filename) {
        if (filename == null || "".equals(filename) || !filename.contains(".") || filename.charAt(0) == '.') {
            return true;
        }
        String extension = filename.substring(filename.lastIndexOf(".")).replaceAll("\\.", "");
        return !"jpg".equals(extension) && !"png".equals(extension);
    }

    //  检查是否不合格
    public static boolean noStandardExtensionWithVideo(String filename) {
        if (filename == null || "".equals(filename) || !filename.contains(".") || filename.charAt(0) == '.') {
            return true;
        }
        String extension = filename.substring(filename.lastIndexOf(".")).replaceAll("\\.", "");
        return !"avi".equals(extension) && !"mp4".equals(extension);
    }

    //  将文件名的后缀修改为 ？？？
    public static String modifyFileExtension(String filename, String toExtension) {
        if (noStandardExtensionWithPhoto(filename) && noStandardExtensionWithVideo(filename)) {
            return null;
        }
        String pre_extension = filename.substring(0, filename.lastIndexOf("."));
        return pre_extension + toExtension;
    }

    public static void main(String[] args) {
        String s = modifyFileExtension("1/1/114b910c5362a0200baceb0baea19d5d/114b910c5362a0200baceb0baea19d5d.mp4", ".avi");
        System.out.println(s);
    }

}

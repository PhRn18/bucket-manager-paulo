package com.project.bucketmanager.Utils;

public class FileUtil {
    public static String getFolderFromKey(String key) {
        int lastSlashIndex = key.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            return key.substring(0, lastSlashIndex);
        }
        return null;
    }
    public static String getExtensionFromKey(String key) {
        int lastDotIndex = key.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < key.length() - 1) {
            return key.substring(lastDotIndex + 1);
        }
        return null;
    }
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    public static String getOriginalFileExtension(String fileName){
        int dashIndex = fileName.indexOf("-");
        int dotIndex = fileName.lastIndexOf(".");

        if (dashIndex != -1 && dotIndex != -1 && dotIndex > dashIndex) {
            return fileName.substring(dashIndex + 1, dotIndex);
        }

        return null;
    }
}

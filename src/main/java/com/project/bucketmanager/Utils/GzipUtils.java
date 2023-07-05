package com.project.bucketmanager.Utils;

import com.project.bucketmanager.ExceptionHandler.Exceptions.FileUploadException;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {
    public static byte[] getCompressedBytesUsingGZIP(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            StreamUtils.copy(inputStream, gzipOutputStream);
        } catch (IOException ex) {
            throw new FileUploadException("Error compressing the file: " + ex.getMessage());
        }


        return outputStream.toByteArray();
    }
}

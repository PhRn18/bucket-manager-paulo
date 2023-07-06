package com.project.bucketmanager.Utils;

import com.project.bucketmanager.Models.BucketDetails;

import java.util.List;
import java.util.regex.Pattern;

public class S3ValidationUtils {
    public static boolean bucketNameMatchRegex(String bucketName){
        String bucketNameRegex = "^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$";
        Pattern pattern = Pattern.compile(bucketNameRegex);
        return pattern.matcher(bucketName).matches();
    }
    public static boolean bucketExists(List<BucketDetails> buckets, String bucketName){
        return buckets
                .stream()
                .map(BucketDetails::getBucketName)
                .toList()
                .contains(bucketName);
    }
}

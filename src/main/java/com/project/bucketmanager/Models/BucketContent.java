package com.project.bucketmanager.Models;

import java.util.Collections;
import java.util.List;

public class BucketContent {
    private final List<Content> objectList;

    public BucketContent(List<Content> objectList) {
        this.objectList = objectList;
    }
    public BucketContent(){
        this.objectList= Collections.emptyList();
    }

    public List<Content> getObjectList() {
        return objectList;
    }
    public static BucketContent buildEmptyResponse(){
        return new BucketContent();
    }
}

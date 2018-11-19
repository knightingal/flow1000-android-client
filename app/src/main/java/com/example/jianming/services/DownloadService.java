package com.example.jianming.services;


import java.util.ArrayList;
import java.util.List;

public class DownloadService extends org.nanjing.knightingal.processerlib.Services.DownloadService{
    private List<Integer> processingIndex = new ArrayList<>();

    public List<Integer> getProcessingIndex() {
        return processingIndex;
    }

    public void setProcessingIndex(List<Integer> processingIndex) {
        this.processingIndex = processingIndex;
    }
}

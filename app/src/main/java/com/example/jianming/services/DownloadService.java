package com.example.jianming.services;

import java.util.ArrayList;
import java.util.List;

public class DownloadService extends org.nanjing.knightingal.processerlib.Services.DownloadService {
    private List<Integer> processingIds = new ArrayList<>();

    public List<Integer> getProcessingIds() {
        return processingIds;
    }

    public void setProcessingIds(List<Integer> processingIds) {
        this.processingIds = processingIds;
    }
}

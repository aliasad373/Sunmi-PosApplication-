package com.mindcrafts.posapp.network.callback;

public interface BatchUploadCallback {
    public void onBatchUploadSuccess(String response);
    public void onBatchUploadError(String msg);
}

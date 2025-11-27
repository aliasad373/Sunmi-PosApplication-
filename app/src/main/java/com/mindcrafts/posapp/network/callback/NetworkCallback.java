package com.mindcrafts.posapp.network.callback;

public interface NetworkCallback {
    public void onCallSuccess(byte[] response);
    public void onError(String msg);

}

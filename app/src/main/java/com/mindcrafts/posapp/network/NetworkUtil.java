package com.mindcrafts.posapp.network;

import android.util.Log;

import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.network.callback.NetworkCallback;
import com.mindcrafts.posapp.utils.ByteUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkUtil {
    public NetworkUtil(){}
    public void HostCall(byte[] request, NetworkCallback callback) {
        String ipAddress = IsoConfig.ip;
        int port = IsoConfig.Port;

        // Run in background thread (IO)
        new Thread(() -> {
            try {
                //TerminalConfig.Loger("Request", utils.byteArrayToHexString(request));
                //String requestToSave = utils.byteArrayToHexString(request);

                // Create socket and connect
                Socket socket = new Socket(ipAddress, port);

                // Send request
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(request);
                outputStream.flush();

                // Read response
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);

                if (bytesRead == -1) {
                    callback.onError("Server not responding");
                    // No response from server
                    //endReversal(message, callBack);
                    // callBack.responseFromServer("2", "3937", "", "Server not responding");
                } else {
                    // Convert response to hex string
                    StringBuilder responseBuilder = new StringBuilder();
                    for (int i = 0; i < bytesRead; i++) {
                        responseBuilder.append(String.format("%02X", buffer[i]));
                    }
                    String response = responseBuilder.toString();
                    callback.onCallSuccess(ByteUtil.hexStr2Bytes(response));
                    //Log.d("MDM", "Response from server: " + response);

                }

                outputStream.close();
                inputStream.close();
                socket.close();

            } catch (Exception e) {
                callback.onError( "Internet issue");
                e.printStackTrace();
            }
        }).start(); // Launch the thread
    }
}

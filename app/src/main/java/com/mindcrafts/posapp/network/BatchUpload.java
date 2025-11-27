package com.mindcrafts.posapp.network;

import android.util.Log;

import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.network.callback.BatchUploadCallback;
import com.mindcrafts.posapp.utils.ByteUtil;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchUpload {

    private final Queue<byte[]> messageQueue = new LinkedList<>();
    private final String serverIp;
    private final int serverPort;
    private Socket socket;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    BatchUploadCallback callback;
    public BatchUpload(List<byte[]> messages, String serverIp, int serverPort, BatchUploadCallback callback) {
        this.messageQueue.addAll(messages);
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.callback =callback;
    }

    public void start() {
        executor.execute(() -> {
            try {
                socket = new Socket(serverIp, serverPort);
                System.out.println("Connected to " + serverIp + ":" + serverPort);
                sendNext();
            } catch (IOException e) {
                System.err.println("Could not connect: " + e.getMessage());
                shutdown();
            }
        });
    }

    private void sendNext() {
        if (messageQueue.isEmpty()) {
            System.out.println("All messages sent and responded!");
            callback.onBatchUploadSuccess("200");
            closeSocket();
            shutdown();
            return;
        }

        byte[] message = messageQueue.poll();

        executor.execute(() -> sendMessage(message, new ResponseCallback() {
            @Override
            public void onResponse(byte[] response) {
                Log.d("320 resp", ByteUtil.bytes2HexStr(response));
                sendNext();
            }

            @Override
            public void onError(Exception e) {
                System.err.println("Error sending message: " + e.getMessage());
                closeSocket();
                shutdown();
            }
        }));
    }

    private void sendMessage(byte[] msg, ResponseCallback callback) {
        try {
            Log.d("message", ByteUtil.bytes2HexStr(msg));
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(msg);
            out.flush();

            byte[] buffer = new byte[1024];
            int len = in.read(buffer); // still blocking, but on background thread
            if (len > 0) {
                byte[] response = Arrays.copyOf(buffer, len);
                callback.onResponse(response);
            } else {
                callback.onError(new IOException("No response"));
            }
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    private void shutdown() {
        executor.shutdown();
    }

    interface ResponseCallback {
        void onResponse(byte[] response);
        void onError(Exception e);
    }
}


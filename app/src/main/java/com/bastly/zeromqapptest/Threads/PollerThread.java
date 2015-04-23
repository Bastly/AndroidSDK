package com.bastly.zeromqapptest.Threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bastly.zeromqapptest.models.Worker;
import com.bastly.zeromqapptest.utils.Constants;

import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by goofyahead on 22/04/15.
 */
public class PollerThread extends Thread {

    private static final String TAG = PollerThread.class.getName();
    private ZMQ.Poller poller;
    private boolean stop = false;
    private ConcurrentHashMap<String, Worker> ttl;
    private HashMap<ZMQ.Socket, String> socketMapToIp;
    private Handler uiHandler;
    private String msg, msg2;
    private int pollerCounter = 0;
    private Worker currentWorker;

    public PollerThread (Handler uiHandler, ConcurrentHashMap<String, Worker> ttl, ZMQ.Poller poller, HashMap<ZMQ.Socket, String> socketMapToIp){
        this.ttl = ttl;
        this.uiHandler = uiHandler;
        this.poller = poller;
        this.socketMapToIp = socketMapToIp;
    }

    public void stopMe() {
        Log.d(TAG, "stop is now true");
        this.stop = true;
    }

    public void setPollerCounter (int value) {
        this.pollerCounter = value;
    }

    @Override
    public void run() {

        Log.d(TAG, "RUNNING WORKER THREAD");
        while (!Thread.currentThread().isInterrupted() && ! stop) {
//            Log.d(TAG, "poller is alive " + pollerCounter);
            if (pollerCounter != 0) {
//                Log.d(TAG, "poller before made poll");
                poller.poll(1000);
                Log.d(TAG, "poller made poll");
                for (int x = 0; x < poller.getSize(); x++) {
                    if (poller.getItem(x) != null && poller.pollin(x)) {
                        Log.d(TAG, "poller pollin " + x);
                        msg = new String(poller.getSocket(x).recv(0));
                        msg2 = poller.getSocket(x).recvStr(ZMQ.DONTWAIT);
                        if (msg.equalsIgnoreCase(Constants.PING)) {
                            // just update the TTL
                            currentWorker = ttl.get(socketMapToIp.get(poller.getSocket(x)));
                            Log.d(TAG, "PING from " + currentWorker.getIp());
                            currentWorker.setTimeStamp(System.currentTimeMillis());
                            ttl.put(socketMapToIp.get(poller.getSocket(x)), currentWorker);
                        } else {
                            Log.d(TAG, "MSG: " + msg + " : " + msg2);

                            Bundle bundle = new Bundle();
                            bundle.putString("message", msg2);
                            Message msg = new Message();
                            msg.setData(bundle);
                            uiHandler.sendMessage(msg);
                        }
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "polling thread set to close, closing everything");

    }

    public void unRegisterWorker (ZMQ.Socket socket, String ip){
        poller.unregister(socket);
        ttl.remove(ip);
        Log.d(TAG, "probably unregistered correctly a socket");
    }

    public void incrementPollerCounter() {
        this.pollerCounter++;
    }
}

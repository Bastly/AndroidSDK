package com.bastly.zeromqapptest.Threads;

import android.util.Log;

import com.bastly.zeromqapptest.interfaces.WorkerLost;
import com.bastly.zeromqapptest.models.Worker;

import org.zeromq.ZMQ;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by goofyahead on 22/04/15.
 */
public class HealthThread extends Thread {

    private boolean stop = false;
    private String TAG = HealthThread.class.getName();
    private ConcurrentHashMap<String, Worker> ttl;
    private WorkerLost listener;

    public HealthThread (ConcurrentHashMap<String, Worker> ttl, WorkerLost listener){
        this.ttl = ttl;
        this.listener = listener;
    }

    public void stopMe() {
        Log.d(TAG, "stop is now true");
        this.stop = true;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && ! stop) {
            for (String socketIp : ttl.keySet()) {
                if (ttl.get(socketIp).getTimeStamp() != -1) {
                    long elapsed = System.currentTimeMillis() - ttl.get(socketIp).getTimeStamp();
                    Log.d(TAG, "Elapsed:" + elapsed + " and stop is: " + stop);
                    if (elapsed > 1000) { // worker may be dead request another one and replace
                        Log.d(TAG, "should disconnect " + socketIp);
                        listener.onWorkerDisconnected(ttl.get(socketIp));
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "being told to just stop and be collected");
    }
}

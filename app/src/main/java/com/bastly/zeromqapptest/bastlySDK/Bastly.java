package com.bastly.zeromqapptest.bastlySDK;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bastly.zeromqapptest.Threads.HealthThread;
import com.bastly.zeromqapptest.Threads.PollerThread;
import com.bastly.zeromqapptest.interfaces.MessageListener;
import com.bastly.zeromqapptest.interfaces.RequestWorker;
import com.bastly.zeromqapptest.interfaces.WorkerLost;
import com.bastly.zeromqapptest.models.Worker;
import com.bastly.zeromqapptest.tasks.ReqAsyncTask;
import com.bastly.zeromqapptest.utils.Constants;
import com.google.gson.Gson;

import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by goofyahead on 18/04/15.
 */
public class Bastly <T> implements WorkerLost {

    private static final String TAG = Bastly.class.getName();
    private PollerThread pollerThread;
    private HealthThread healthTrhead;
    private ZMQ.Context context = ZMQ.context(1);
    private ZMQ.Poller poller;
    private HashMap<String, ZMQ.Socket> dataSockets;
    private HashMap<String, ZMQ.Socket> pingSockets;
    private String from = "goofyahead";
    private String apiKey = "123456";
    private Handler uiHandler;
    private Gson gson = new Gson();
    private MessageListener listener;
    private ConcurrentHashMap<String, Worker> ttl;
    private HashMap<ZMQ.Socket, String> socketMapToIp;

    public Bastly (String userId, String apiKey, Context context, final MessageListener listener, final Class<T> messageClass) {

        this.apiKey = apiKey;
        this.from = userId;
        this.listener = listener;

        uiHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "on UI thread msg " );
                listener.onMessageReceived("channel", gson.fromJson(msg.getData().getString("message"), messageClass) );
            }
        };
    }

    public void registerChannel (final String channel) {
        // ask for a worker ip in Thread, get callback
        new ReqAsyncTask(new RequestWorker() {
            @Override
            public void onWorkerAssigned(final String ip) {
                Log.d(TAG, "GIVEN IP TO CONNECT TO: " + ip);
                // we now have the ip check if we got it or create a new one
                if (dataSockets.get(ip) != null) {
                    // subscribe the new channel and we are good to go
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Worker worker = ttl.get(ip);
                            if (! worker.getChannels().contains(channel)) {
                                dataSockets.get(ip).subscribe(channel.getBytes());
                                worker.addChannel(channel);
                                Log.d(TAG, "existing socket, adding new channel to listen: " + channel);
                            } else {
                                Log.d(TAG, "already has the channel, should recive pings soon");
                            }
                        }
                    }).start();
                } else { // create the new socket, add the ping listener and add to poller
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // create Data socket
                            Log.d(TAG, "Creating socket and subscribing to " + channel);
                            dataSockets.put(ip, context.socket(ZMQ.SUB));
                            dataSockets.get(ip).connect("tcp://" + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_MESSAGES);
                            dataSockets.get(ip).subscribe(channel.getBytes());
                            socketMapToIp.put(dataSockets.get(ip), ip);

                            Worker worker = new Worker(ip);
                            worker.addChannel(channel);
                            worker.setTimeStamp(System.currentTimeMillis());
                            ttl.put(ip, worker);

                            poller.register(dataSockets.get(ip), ZMQ.Poller.POLLIN);

                            // create ping socket
                            pingSockets.put(ip, context.socket(ZMQ.SUB));
                            pingSockets.get(ip).connect("tcp://" + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_PINGS);
                            pingSockets.get(ip).subscribe("ping".getBytes());

                            socketMapToIp.put(pingSockets.get(ip), ip);

                            poller.register(pingSockets.get(ip), ZMQ.Poller.POLLIN);

                            pollerThread.incrementPollerCounter();
                        }
                    }).start();
                }
            }
        }, this.from, channel, this.apiKey).execute();
    }

    public void unResgiterChannel (final String channel) {

    }

    public void onResume() {
        Log.d(TAG, "on Resume called");
        poller = new ZMQ.Poller(10);
        ttl = new ConcurrentHashMap<>();
        dataSockets = new HashMap<>();
        pingSockets = new HashMap<>();
        socketMapToIp = new HashMap<>();

        if (healthTrhead == null || ! healthTrhead.isAlive()) {
            Log.d(TAG, "creating health thread");
            healthTrhead = new HealthThread(ttl, this);
        }

        if (pollerThread == null || !pollerThread.isAlive()) {
            Log.d(TAG, "creating poller thread");
            pollerThread = new PollerThread(uiHandler, ttl, poller, socketMapToIp);
        }

        pollerThread.start();
        healthTrhead.start();
    }

    public void onPause() {
        Log.d(TAG, "on Pause called");
        pollerThread.stopMe();
        pollerThread.setPollerCounter(0);
        healthTrhead.stopMe();
    }

    @Override
    public void onWorkerDisconnected(Worker deadWorker) {
        ZMQ.Socket deadPingSocket = pingSockets.get(deadWorker.getIp());
        ZMQ.Socket deadDataSocket = dataSockets.get(deadWorker.getIp());
        dataSockets.remove(deadWorker.getIp());
        pingSockets.remove(deadWorker.getIp());
        pollerThread.unRegisterWorker(deadDataSocket, deadWorker.getIp());
        pollerThread.unRegisterWorker(deadPingSocket, deadWorker.getIp());

        resetVars();

        // register for each channel the worker was subscribed to. Poller should be notified too.
        for (String channel : deadWorker.getChannels()){
            registerChannel(channel);
        }
    }

    private void resetVars(){
        poller = new ZMQ.Poller(10);
        ttl = new ConcurrentHashMap<>();
        dataSockets = new HashMap<>();
        pingSockets = new HashMap<>();
        socketMapToIp = new HashMap<>();

        healthTrhead.stopMe();
        pollerThread.stopMe();

        Log.d(TAG, "creating health thread");
        healthTrhead = new HealthThread(ttl, this);

        Log.d(TAG, "creating poller thread");
        pollerThread = new PollerThread(uiHandler, ttl, poller, socketMapToIp);

        pollerThread.start();
        healthTrhead.start();
    }
}

package com.bastly.bastlysdk.tasks;

import android.os.AsyncTask;
import android.util.Log;


import com.bastly.bastlysdk.utils.Constants;

import org.zeromq.ZMQ;

/**
 * Created by goofyahead on 15/04/15.
 */
public class SubZeroTask extends AsyncTask<String, Void, String> {

    private static final String TAG = SubZeroTask.class.getName();
    private final String ip;
    private final String me;

    public SubZeroTask(String ip, String me) {
        this.ip = ip;
        this.me = me;
    }


    @Override
    protected String doInBackground(String... params) {

        ZMQ.Context context = ZMQ.context(1);


        ZMQ.Socket socketPing = context.socket(ZMQ.SUB);
        socketPing.connect("tcp://" + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_PINGS);

        socketPing.subscribe("ping".getBytes());

        ZMQ.Poller poller = new ZMQ.Poller(10);
        poller.register(socketPing,ZMQ.Poller.POLLIN);

        String msg;
        while(!Thread.currentThread ().isInterrupted ()){
            poller.poll();

            for (int x = 0; x < poller.getSize(); x++) {
                if (poller.getItem(x) != null) {
                    msg = new String(poller.getItem(x).getSocket().recv(ZMQ.DONTWAIT));
                    Log.d(TAG, "MSG: " + msg);
                }
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        //uiThreadHandler.sendMessage(Util.bundledMessage(uiThreadHandler, result));
    }
}
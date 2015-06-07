package com.bastly.bastlysdk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.bastly.bastlysdk.models.Worker;
import com.bastly.bastlysdk.utils.Constants;

import org.zeromq.ZMQ;

/**
 * Created by goofyahead on 24/02/15.
 */
public class PingServerAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = ReqAsyncTask.class.getName();
    private final String from;
    private final String to;
    private final String apikey;

    public PingServerAsyncTask(String from, String to, String apiKey) {
        Log.d(TAG, "creating ping task " + apiKey);
        this.from = from;
        this.to = to;
        this.apikey = apiKey;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "initializing ping task");
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://" + Constants.CURACA_IP + ":" + Constants.PORT_REQ_REP_ATAHUALPA_CURACA_COMM);

        Log.d(TAG, "sending ping to curaca on " + Constants.CURACA_IP + ":" + Constants.PORT_REQ_REP_ATAHUALPA_CURACA_COMM);
        socket.send("PING", ZMQ.SNDMORE);
        socket.send(this.to, ZMQ.SNDMORE);
        socket.send(this.from, ZMQ.SNDMORE);
        socket.send(this.apikey, 0);

        String result = new String(socket.recv(0));
        Log.d(TAG, "Resutl " + result);
        String message = "";

        while (socket.hasReceiveMore()) {
            message += new String(socket.recv(0));
        }

        Log.d(TAG, "message: " + message);
        socket.close();
        context.term();

        return result;
    }
}
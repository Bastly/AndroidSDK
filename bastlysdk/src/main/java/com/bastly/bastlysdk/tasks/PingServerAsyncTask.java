package com.bastly.bastlysdk.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.bastly.bastlysdk.interfaces.RequestWorker;
import com.bastly.bastlysdk.models.Worker;
import com.bastly.bastlysdk.utils.Constants;
import com.google.gson.Gson;

import org.zeromq.ZMQ;

/**
 * Created by goofyahead on 24/02/15.
 */
public class ReqAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = ReqAsyncTask.class.getName();
    private final String from;
    private final String to;
    private final String apikey;
    private Worker givenWorker;

    public ReqAsyncTask(String from, String to, String apiKey) {
        this.from = from;
        this.to = to;
        this.apikey = apiKey;
    }

    @Override
    protected String doInBackground(String... params) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.connect("tcp://" + Constants.ATAHUALPA_IP + ":" + Constants);

        Log.d(TAG, "sending string to atahualpa on " + Constants.ATAHUALPA_IP + ":" + Constants.PORT_REQ_REP_ATAHUALPA_CLIENT_REQUEST_WORKER);
        socket.send("subscribe", ZMQ.SNDMORE);
        socket.send(this.to, ZMQ.SNDMORE);
        socket.send(this.from, ZMQ.SNDMORE);
        socket.send(this.apikey, ZMQ.SNDMORE);
        socket.send("ZEROMQ", 0);

        String result = new String(socket.recv(0));
        Log.d(TAG, "Resutl " + result);
        String message = "";

        while (socket.hasReceiveMore()) {
            message += new String(socket.recv(0));
        }

        Log.d(TAG, "message: " + message);

        if (!result.equalsIgnoreCase("400")) {
            Gson gson = new Gson();
            givenWorker = gson.fromJson(message, Worker.class);

            Log.d(TAG, "RESULT IS:" + givenWorker.getIp());
        }

        socket.close();
        context.term();

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("worker", givenWorker);
//        Message msg = new Message();
//        msg.setData(bundle);
//        uiThreadHandler.sendMessage(msg);
        callback.onWorkerAssigned(givenWorker.getIp());
    }
}
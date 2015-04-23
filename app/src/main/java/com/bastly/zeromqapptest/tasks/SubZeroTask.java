package com.bastly.zeromqapptest.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.bastly.zeromqapptest.utils.Constants;

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
    //private final Handler uiThreadHandler;

    //public ZeroMQMessageTask(Handler uiThreadHandler) {
    //    this.uiThreadHandler = uiThreadHandler;
    //}

    @Override
    protected String doInBackground(String... params) {

        ZMQ.Context context = ZMQ.context(1);

//        ZMQ.Socket socket = context.socket(ZMQ.SUB);
        ZMQ.Socket socketPing = context.socket(ZMQ.SUB);
        socketPing.connect("tcp://" + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_PINGS);
//        socket.subscribe(me.getBytes());
        socketPing.subscribe("ping".getBytes());

        ZMQ.Poller poller = new ZMQ.Poller(10);
        poller.register(socketPing,ZMQ.Poller.POLLIN);

        String msg;
        while(!Thread.currentThread ().isInterrupted ()){
            poller.poll();
//            Log.d(TAG, "next " + poller.getNext());

            for (int x = 0; x < poller.getSize(); x++) {
                if (poller.getItem(x) != null) {
                    msg = new String(poller.getItem(x).getSocket().recv(ZMQ.DONTWAIT));
                    Log.d(TAG, "MSG: " + msg);
                }
            }
        }
//        socket.connect("tcp://" + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_MESSAGES);


//        Log.d(TAG, "subscribed to " + ip + ":" + Constants.PORT_PUB_SUB_CHASKI_CLIENT_MESSAGES);
//        String msg, msg2;
//
//        while(true) {
////            msg = new String(socket.recv(0));
////            Log.d("zero", msg);
//            msg2 = socketPing.recvStr();
//            Log.d("ping", msg2);
//        }
        //socket.close();
        //context.term();

        /*
        //  Socket to talk to server
        System.out.println("Connecting to hello world server…");
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://54.154.194.204:3001");
        for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
            String request = "Hello";
            Log.d("zero", "Sending Hello " + requestNbr);
            requester.send(request.getBytes(), 0);
            byte[] reply = requester.recv(0);
            Log.d("zero", new String(reply) + " " + requestNbr);
        }
        requester.close();
        context.term();
        */




        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        //uiThreadHandler.sendMessage(Util.bundledMessage(uiThreadHandler, result));
    }
}
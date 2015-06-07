package com.bastly.bastlysdk.models;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by goofyahead on 15/04/15.
 */
public class Worker implements Serializable{

    private String workerIp;
    private LinkedList<String> channels = new LinkedList<>();
    private long timeStamp = -1;

    public Worker(String ip) {
        this.workerIp = ip;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void addChannel (String channel) {
        channels.add(channel);
    }

    public LinkedList<String> getChannels (){
        return channels;
    }

    public String getIp() {
        return workerIp;
    }

    public void setIp(String ip) {
        this.workerIp = ip;
    }
}

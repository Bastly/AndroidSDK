package com.bastly.bastlysdk.interfaces;

/**
 *
 * @param <T> is the class that you want to be populated when you receive a callback
 */
public interface MessageListener <T>{
    void onMessageReceived(String channel, T message);
}

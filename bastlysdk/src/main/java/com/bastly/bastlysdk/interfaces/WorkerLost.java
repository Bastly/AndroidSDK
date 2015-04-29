package com.bastly.bastlysdk.interfaces;


import com.bastly.bastlysdk.models.Worker;

/**
 * Created by goofyahead on 22/04/15.
 */
public interface WorkerLost {
    void onWorkerDisconnected(Worker deadWorker);
}

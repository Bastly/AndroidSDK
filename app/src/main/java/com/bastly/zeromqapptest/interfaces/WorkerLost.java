package com.bastly.zeromqapptest.interfaces;

import com.bastly.zeromqapptest.models.Worker;

/**
 * Created by goofyahead on 22/04/15.
 */
public interface WorkerLost {
    void onWorkerDisconnected(Worker deadWorker);
}

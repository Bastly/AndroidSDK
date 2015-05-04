package com.bastly.bastlysdk.interfaces;

import com.bastly.bastlysdk.models.Orion;

/**
 * Created by goofyahead on 5/4/15.
 */
public interface OrionListener {
    void onOrionMessageReceived(String channel, Orion message);
}

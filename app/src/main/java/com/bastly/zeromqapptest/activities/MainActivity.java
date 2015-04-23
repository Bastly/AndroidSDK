package com.bastly.zeromqapptest.activities;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bastly.zeromqapptest.R;
import com.bastly.zeromqapptest.bastlySDK.Bastly;
import com.bastly.zeromqapptest.interfaces.MessageListener;
import com.bastly.zeromqapptest.models.Play;
import com.bastly.zeromqapptest.models.Worker;
import com.bastly.zeromqapptest.tasks.ReqAsyncTask;
import com.bastly.zeromqapptest.tasks.SubZeroTask;


public class MainActivity extends ActionBarActivity implements MessageListener <Play>{
    private static final String TAG = MainActivity.class.getName();
    private Handler handler;
    private static final String FROM = "goofyahead";
    private static final String TO = "goofyahead";
    private static final String APIKEY = "apikey";
    private Bastly <Play> bastly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Activity on create");
        bastly = new Bastly(FROM, APIKEY, this, this, Play.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity RESUMED activity asdf");
        bastly.onResume();
        bastly.registerChannel("goofyahead");
    }

    @Override
    protected void onPause() {
        super.onPause();
        bastly.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageReceived(String channel, Play message) {
        Log.d(TAG, "Spell: " + message.getSpell());

    }
}

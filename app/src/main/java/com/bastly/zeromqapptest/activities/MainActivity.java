package com.bastly.zeromqapptest.activities;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bastly.bastlysdk.Bastly;
import com.bastly.bastlysdk.interfaces.MessageListener;
import com.bastly.bastlysdk.interfaces.OrionListener;
import com.bastly.bastlysdk.models.Attributes;
import com.bastly.bastlysdk.models.Orion;
import com.bastly.zeromqapptest.R;
import com.bastly.zeromqapptest.models.Play;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;


public class MainActivity extends ActionBarActivity implements MessageListener<Play>, OrionListener {
    private static final String TAG = MainActivity.class.getName();
    private Handler handler;
    private static final String FROM = "goofyahead";
    private static final String TO = "goofyahead";
    private static final String APIKEY = "apikey";
    private Bastly<Orion> bastly;

    private SpeedometerGauge speedometer1, speedometer2, speedometer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Activity on create");
        bastly = new Bastly(FROM, APIKEY, this, Play.class);

        speedometer1 = (SpeedometerGauge) findViewById(R.id.speedometer1);
        speedometer2 = (SpeedometerGauge) findViewById(R.id.speedometer2);
        speedometer3 = (SpeedometerGauge) findViewById(R.id.speedometer3);

        speedometer1.setLabelConverter(new SpeedometerGauge.LabelConverter() {

            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer1.setMaxSpeed(100);
        speedometer1.setMajorTickStep(10);
        speedometer1.setLabelTextSize(20);

        // Configure value range colors
        speedometer1.addColoredRange(10, 40, Color.GREEN);
        speedometer1.addColoredRange(40, 70, Color.YELLOW);
        speedometer1.addColoredRange(70, 100, Color.RED);

        speedometer2.setLabelConverter(new SpeedometerGauge.LabelConverter() {

            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer2.setMaxSpeed(100);
        speedometer2.setMajorTickStep(10);
        speedometer2.setLabelTextSize(20);

        // Configure value range colors
        speedometer2.addColoredRange(10, 40, Color.GREEN);
        speedometer2.addColoredRange(40, 70, Color.YELLOW);
        speedometer2.addColoredRange(70, 100, Color.RED);


        speedometer3.setLabelConverter(new SpeedometerGauge.LabelConverter() {

            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer3.setMaxSpeed(100);
        speedometer3.setMajorTickStep(10);
        speedometer3.setLabelTextSize(20);

        // Configure value range colors
        speedometer3.addColoredRange(10, 40, Color.GREEN);
        speedometer3.addColoredRange(40, 70, Color.YELLOW);
        speedometer3.addColoredRange(70, 100, Color.RED);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity RESUMED activity asdf");
        bastly.onResume();
        bastly.registerChannel("testOffice");
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

    }

    @Override
    public void onOrionMessageReceived(String channel, Orion message) {
        for (Attributes attribute : message.getAttributes()) {
            if (attribute.getName().equalsIgnoreCase("temperature:Kitchen")) {
                speedometer1.setSpeed(Float.parseFloat(attribute.getValue()), 200, 0);
            } else if (attribute.getName().equalsIgnoreCase("temperature:Entrance")) {
                speedometer2.setSpeed(Float.parseFloat(attribute.getValue()), 200, 0);
            } else if (attribute.getName().equalsIgnoreCase("temperature:MeetingRoom")) {
                speedometer3.setSpeed(Float.parseFloat(attribute.getValue()), 200, 0);
            }
        }
    }
}

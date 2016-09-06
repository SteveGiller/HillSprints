package uk.co.stevegiller.deadmeatgf.hillsprints;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Steve Giller on 05/09/2016.
 * Credit to scottt (http://stackoverflow.com/users/2825864/scottt)
 * via http://stackoverflow.com/questions/22496863/how-to-run-countdowntimer-in-a-service-in-android
 **/

public class BroadcastCountdown extends Service {

    private static final String TAG = "BroadcastCountdown";
    public static final String COUNTDOWN = "uk.co.stevegiller.deadmeatgf.hillsprints.countdown";
    public static final String START_TIME = "startValue";
    public static final String INTERVAL = "interval";
    public static final String ANNOUNCE_HALFWAY = "halfway";
    private static final long DEFAULT_START_TIME = 30000;
    private static final long DEFAULT_INTERVAL = 1000;
    private static final boolean DEFAULT_ANNOUNCEMENT = false;
    public static final String TICK_NOTIFIER = "countdown";
    public static final String TICK_MESSAGE = "message";

    private long startTime;
    private long interval;
    private boolean announceHalfway;

    Intent cbi = new Intent(COUNTDOWN);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = intent.getLongExtra(START_TIME, DEFAULT_START_TIME) + 500;
        interval = intent.getLongExtra(INTERVAL, DEFAULT_INTERVAL);
        announceHalfway = intent.getBooleanExtra(ANNOUNCE_HALFWAY, DEFAULT_ANNOUNCEMENT);

        Log.i(TAG, "Starting Timer ...");
        Log.i(TAG, "Counting down from " + startTime + " in " + interval / 1000 + " second intervals.");
        if (announceHalfway) {
            Log.i(TAG, "There will be a halfway point announcement.");
        }

        cdt = new CountDownTimer(startTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                if (announceHalfway) {
                    if (millisUntilFinished <= startTime / 2) {
                        cbi.putExtra(TICK_MESSAGE, "We have reached the halfway point.");
                        announceHalfway = false;
                    }
                } else {
                    cbi.removeExtra(TICK_MESSAGE);
                }
                cbi.putExtra(TICK_NOTIFIER, millisUntilFinished);
                sendBroadcast(cbi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished.");
                cbi.putExtra(TICK_MESSAGE, "Countdown completed.");
                cbi.putExtra(TICK_NOTIFIER, 0);
                sendBroadcast(cbi);
            }
        };

        cdt.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Log.i(TAG, "Timer cancelled.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

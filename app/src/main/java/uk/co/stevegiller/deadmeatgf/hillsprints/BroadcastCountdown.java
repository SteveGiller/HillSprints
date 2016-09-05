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
 */

public class BroadcastCountdown extends Service {

    private static final String TAG = "BroadcastCountdown";
    public static final String COUNTDOWN = "uk.co.stevegiller.deadmeatgf.hillsprints.countdown";
    Intent cbi = new Intent(COUNTDOWN);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting Timer ...");

        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                cbi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(cbi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished.");
            }
        };

        cdt.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

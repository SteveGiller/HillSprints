package uk.co.stevegiller.deadmeatgf.hillsprints;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class SprintActivity extends Activity {

    private static final String TAG = "SprintActivity";
    private long startTime = 60000;
    private long interval = 2000;
    private boolean announce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        Intent countdownTimer = new Intent(this, BroadcastCountdown.class);
        countdownTimer.putExtra(BroadcastCountdown.START_TIME, startTime);
        countdownTimer.putExtra(BroadcastCountdown.INTERVAL, interval);
        countdownTimer.putExtra(BroadcastCountdown.ANNOUNCE_HALFWAY, announce);
        startService(countdownTimer);
        Log.i(TAG, "Started service");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(cbr);
        Log.i(TAG, "Unregistered broadcast receiver.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(cbr, new IntentFilter(BroadcastCountdown.COUNTDOWN));
        Log.i(TAG, "Registered broadcast receiver.");
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(cbr);
            Log.i(TAG, "Unregistered broadcast receiver.");
        } catch (Exception e) {
            // Receiver was probably already stopped by onPause()
            Log.i(TAG, "Unable to unregister broadcast receiver - this was probably already actioned in the onPause() event.");
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BroadcastCountdown.class));
        Log.i(TAG, "Stopped service.");
        super.onDestroy();
    }

    private BroadcastReceiver cbr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra(BroadcastCountdown.TICK_NOTIFIER, 0);
            String message = intent.getStringExtra(BroadcastCountdown.TICK_MESSAGE);
            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000 + "[" + millisUntilFinished + "ms]");
            if (message != null) {
                Log.i(TAG, "We have reached the halfway point.");
            }
        }
    }
}

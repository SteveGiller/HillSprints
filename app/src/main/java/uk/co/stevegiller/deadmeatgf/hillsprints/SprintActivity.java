package uk.co.stevegiller.deadmeatgf.hillsprints;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;

public class SprintActivity extends Activity {

    private static final String TAG = "SprintActivity";
    private static final String WAKELOCK = "wakelock";
    private long startTime = 60000;
    private long interval = 2000;
    private boolean announce = true;

    private TextView timerText;
    private TextView messageText;

    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);

        timerText = (TextView) findViewById(R.id.textViewCountdown);
        messageText = (TextView) findViewById(R.id.textViewMessage);

        powerManager = (PowerManager) getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK);

        Intent countdownTimer = new Intent(this, BroadcastCountdown.class);
//        countdownTimer.putExtra(BroadcastCountdown.START_TIME, startTime);
//        countdownTimer.putExtra(BroadcastCountdown.INTERVAL, interval);
        countdownTimer.putExtra(BroadcastCountdown.ANNOUNCE_HALFWAY, announce);
        Log.i(TAG, "Acquiring wakelock.");
        wakeLock.acquire();
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
        wakeLock.release();
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
            timerText.setText(String.valueOf(millisUntilFinished / 1000));
            if (message == null) {
                messageText.setText("");
            } else {
                Log.i(TAG, message);
                messageText.setText(message);
            }
            if (millisUntilFinished / 1000 == 0) {
                Log.i(TAG, "Releasing wakelock.");
                wakeLock.release();
            }
        }
    }
}

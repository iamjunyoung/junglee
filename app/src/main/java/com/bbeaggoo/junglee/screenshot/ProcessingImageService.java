package com.bbeaggoo.junglee.screenshot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ProcessingImageService extends Service {
    Thread mThread;
    static int THREAD_SLEEP_TIME = 3000;
    static String TAG = "ProcessingImageService";

    Runnable worker = new Runnable() {
        public void run() {
            runGet();
        }
    };

    public ProcessingImageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service onCreate()");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Service onStartCommand()");

        mThread = new Thread(worker);
        mThread.setDaemon(true);
        mThread.start();
        return START_STICKY;
    }
    private void runGet() {
        Log.i(TAG, "Observing is started");


        try {
            while(!Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "I am observer hihi");
                Thread.sleep(THREAD_SLEEP_TIME);
            }
        } catch(InterruptedException e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

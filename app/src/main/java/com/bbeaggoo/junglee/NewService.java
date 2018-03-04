package com.bbeaggoo.junglee;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class NewService extends Service {
    public static String TAG = "APVM";
    Thread mThread;
    private static int THREAD_SLEEP_TIME = 3000;
    private final static String SCREENSHOTS_DIR_NAME = "Screenshots";
    MyContentObserver co;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(TAG, "Service is started onStartCommand()");
        mThread = new Thread(worker);
        mThread.setDaemon(true);
        mThread.start();
        return START_REDELIVER_INTENT;
    }

    Runnable worker = new Runnable() {
        public void run() {
            runGet();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //FileNotifier fileObserver = new FileNotifier();
        //fileObserver.stopWatching();
        Log.v(TAG, "FileObserver is stopWatching()");

        getContentResolver().unregisterContentObserver(co);

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
            Log.v(TAG, "Thread is stopped in onDestroy()");
        }
        //handler.removeMessages(MSG_WORK);
    }

    private void runGet() {
        //File myFile = new File("storage/self/primary/Pictures/Screenshots");
        File myFile = new File("sdcard/Pictures/Screenshots");
        //File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
        //File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        //File myFile = new File("sdcard/DCIM/Screenshots"); // L
        Log.i(TAG, "Observing is clicked...  path : " + myFile.getPath());
        NewScreenshotObserver observer = new NewScreenshotObserver(myFile.getAbsolutePath());
        observer.startWatching();
        Log.i(TAG, "Oberving is started");

        //monitorAllFiles(myFile);
        Handler handler = new Handler(Looper.getMainLooper());
        co = new MyContentObserver(this, handler);
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, co);

        try {
            while(!Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "I am observer sdcard");
                Thread.sleep(THREAD_SLEEP_TIME);
            }
        } catch(InterruptedException e) {
        }
    }

    private static class NewScreenshotObserver extends FileObserver {
        private String mAddedPath = null;

        NewScreenshotObserver(String path) {
            super(path, FileObserver.CREATE);
        }

        @Override
        public void onEvent(int event, String path) {
            Log.d(TAG, "hihi");
            Log.d(TAG, String.format("Detected new file added %s", path));
            synchronized (this) {
                mAddedPath = path;
                notify();
            }
        }
    }
}
package com.bbeaggoo.junglee;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.bbeaggoo.junglee.db.DataBases;
import com.bbeaggoo.junglee.db.DbOpenHelper;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CilpboardListenerService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    public static String TAG = "ClipboardListener";
    Thread mThread;
    public static ArrayList<ListItem> mySaveList = new ArrayList<>();
    static int THREAD_SLEEP_TIME = 3000;

    ClipboardManager clipBoard;

    private static DbOpenHelper mDbOpenHelper;
    private static Cursor mCursor;

    // About Tess-two
    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로
    //[출처] [안드로이드 스튜디오] 안드로이드 OCR 앱 만들기|작성자 코스모스


    public CilpboardListenerService() {

    }

    @Override
    public void onPrimaryClipChanged() {
        Log.i("JYN", "onPrimaryClipChanged called");

        if (clipBoard != null && clipBoard.getPrimaryClip() != null) {
            ClipData data = clipBoard.getPrimaryClip();
            Log.i("JYN", "onPrimaryClipChanged data : " + data.getItemAt(0));

            String type = clipBoard.getPrimaryClipDescription().getMimeType(0);
            saveClipItem(clipBoard.getPrimaryClip().getItemAt(0), type);

            // 한번의 복사로 복수 데이터를 넣었을 수 있으므로, 모든 데이터를 가져온다.

            int dataCount = data.getItemCount();
            int size = clipBoard.getPrimaryClip().getItemCount();

            Log.i("JYN", "dataCount : " + dataCount + "    size : " + size);
            for (int i = 0 ; i < dataCount ; i++) {
                Log.i("JYN", "clip data - item : "+data.getItemAt(i).coerceToText(this));
            }

        } else {
            Log.i("JYN", "No Manager or No Clip data");
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "Service onCreate()");

        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(this);
        //clipBoard.a
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.v(TAG, "Service is started onStartCommand()");

        /////////////////////////////////// About Tess-two /////////////////////////////////////////
        //이미지 디코딩을 위한 초기화
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.sample4); //샘플이미지파일
        //언어파일 경로
        datapath = getFilesDir()+ "/tesseract/";
        Log.i("JYN", "datapath : " + datapath);
        String testPath = Environment.getExternalStorageDirectory().toString() + "/tess/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"));

        //Tesseract API
        String lang = "eng+kor";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
        //[출처] [안드로이드 스튜디오] 안드로이드 OCR 앱 만들기|작성자 코스모스
        ////////////////////////////////////////////////////////////////////////////////////////////

        mThread = new Thread(worker);
        mThread.setDaemon(true);
        mThread.start();
        return START_STICKY;
    }

    private void saveClipItem(ClipData.Item item, String type) {
        if( item != null && type != null ) {
            Log.i("ClipboardListener", "type : " + type + "    item : " + item);

            if (ClipDescription.MIMETYPE_TEXT_HTML.equals(type)) {
                Log.i("ClipboardListener", "HTML");
            } else if (ClipDescription.MIMETYPE_TEXT_INTENT.equals(type)) {
                Log.i("ClipboardListener", "INTENT");
            } else if (ClipDescription.MIMETYPE_TEXT_PLAIN.equals(type)) {
                Log.i("ClipboardListener", "PLAIN");
            } else if (ClipDescription.MIMETYPE_TEXT_URILIST.equals(type)) {
                Log.i("ClipboardListener", "URLLIST");
            } else {
                Log.i("ClipboardListener", "Else");
            }

            if (item.getText() != null) {
                String clipboardText = item.getText().toString();

                //이 url을 ImageDownloaderTask로 보내서
                //imgUrl(bitmap image), title, date등을 get해야 함.
                ImageDownloaderTask idlt = new ImageDownloaderTask(this);
                idlt.execute(clipboardText);
                Log.i("ClipboardListener", clipboardText + " is add to urlList");

            } else {
                Uri uri = item.getUri();
                Log.i("ClipboardListener", "Uri : " + uri);
                Log.i("ClipboardListener", "path : " + uri.getPath() + "    toString : " + uri.getPath().toString());
                saveSceenCaptureImage(uri);
            }
        }
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
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;

            mySaveList.clear();
            clipBoard.removePrimaryClipChangedListener(this);
            Log.v(TAG, "Thread is stopped in onDestroy()");
        }
    }

    private void runGet() {
        Log.i(TAG, "Observing is started");

        //데이터베이스 생성(파라메터 Context) 및 오픈
        mDbOpenHelper = new DbOpenHelper(this);
        //mDbOpenHelperForCategory = new DbOpenHelperForCategory(this);
        try {
            mDbOpenHelper.open();
            //mDbOpenHelperForCategory.open();
            Log.i(TAG, "DB open()");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "And call doWhileCursorToArray()");
        doWhileCursorToArray(this);
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Log.i(TAG, "I am observer hihi");
                Thread.sleep(THREAD_SLEEP_TIME);
            }
        } catch(InterruptedException e) {
        }
    }

    //doWhile문을 이용하여 Cursor에 내용을 다 InfoClass에 입력 후 InfoClass를 ArrayList에 Add
    public void doWhileCursorToArray(Context context) {

        mCursor = null;
        //DB에 있는 모든 컬럼을 가져옴
        mCursor = mDbOpenHelper.getAllColumns();
        //컬럼의 갯수 확인
        Log.i(TAG, "Count = " + mCursor.getCount());

        while (mCursor.moveToNext()) {
            //InfoClass에 입력된 값을 압력

            String imgTemp = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE));
            Drawable d = null;
            //아래의 image loading과정은 하지 않기로 한다.
            //DB에서 path를 읽어 이미지를 loading하는 아래의 과정은 memory를 너무 많이 잡아먹어 OutOfMemory를 유발시킨다.
            //GridView에서 Glide api를 통해 이미지를 로딩하면 되기 때문에 아래 과정은 필요가 없고
            //ListItem의 나머지 요소들에 대해서도 차차 아래의 loading과정을 없애고 DB에서 직접 읽는 방식으로 수정하자.
            /*
            if (imgTemp != null && imgTemp.contains("content://")) {
                //uri case
                Log.i(TAG, "Uri case. imgTemp : " + imgTemp);
                Bitmap bm = null;
                try {
                    bm = Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imgTemp));
                    d = new BitmapDrawable(context.getResources(), bm);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                //web page case
                Log.i(TAG, "Web case. imgTemp : " + imgTemp);
                //d = Drawable.createFromPath(imgTemp);
                //여기서 drawable을 downgrade해서 생성하자
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap src = BitmapFactory.decodeFile(imgTemp, options);
                d = new BitmapDrawable(getResources(), src);
            }
            */
            /*
            if (mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE)).contains(".jpg")) {
                Log.i(TAG, "orig : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE)).contains(".jpg"));
                Uri uri = getImageContentUri(context, new File(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE))));
                Log.i(TAG, "orig to uri : " + uri.toString());
                Boolean update = mDbOpenHelper.updateColumn2(mCursor.getLong(mCursor.getColumnIndex(DataBases.CreateDB._ID)), uri.toString());
                Log.i(TAG, "update : " + update);
            }
            */
            Log.i(TAG, "_id : " + mCursor.getLong(mCursor.getColumnIndex(DataBases.CreateDB._ID)) +
                       "    category : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.CATEGORY)) +
                       "    folder : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FOLDER)));
            boolean isCategory = "no".equals(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.ISCATEGORY)))? false : true;
            boolean isFolder = "no".equals(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.ISFOLDER)))? false : true;
            if (!isCategory && !isFolder) {
                ListItem listItem = new ListItem(mCursor.getLong(mCursor.getColumnIndex(DataBases.CreateDB._ID)),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.TITLE)),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.URL)),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.DESC)),
                        d,  //path에 있는 jpg파일을 drawable로 만들어야함
                        Uri.parse(mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE))),
                        //Uri.parse(imgTemp),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.CATEGORY)),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FOLDER)),
                        mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.DATE)));
                Log.i(TAG, "doWhileCursorToArray() : " + listItem + " : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.URL))
                    + "    category : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.CATEGORY)));
                //입력된 값을 가지고 있는 InfoClass를 InfoArray에 add
                Log.i(TAG, "Image : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.IMAGE)));

                mySaveList.add(listItem);
            }
        }
        //Cursor 닫기
        mCursor.close();
    }

    private void saveSceenCaptureImage(Uri uri) {

        Bitmap bm = null;
        Drawable d = null;
        String OCRResultString = null;

        Bitmap bmForTessTwo = null;
        try {
            bm = Images.Media.getBitmap(getContentResolver(), uri);
            //bmForTessTwo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            d = new BitmapDrawable(getResources(), bm);
            Log.i(TAG, "saveSceenCaptureImage bitmap : " + bm + "    drawable : " + d + "    uri : " + uri);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        OCRResultString = processImage(bm);
        //여기서 tess-two처리를 하자. 시간이 얼마나 걸리려나??
        //여기 비동기로 처리해야 함.

        //두번째 parameter인 title, 첫번째 parameter 인 title에 tess-two결과를 입력하자
        ListItem listItem = new ListItem(-1, OCRResultString, null, null, d, uri, null, null, null);
        long id = mDbOpenHelper.insertColumn(OCRResultString, "", "", uri.toString(), "", "", "", "no", "no");
        Log.i(TAG, "saveSceenCaptureImage to : " + id);
        Log.i(TAG, "saveSceenCaptureImage OCR result : " + OCRResultString);
        listItem.setId(id);
        mySaveList.add(listItem);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    //check file on the device
    private void checkFile(File dir) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }

            String dataFilePathForKor = datapath+ "/tessdata/kor.traineddata";
            File dataFileForKor = new File(dataFilePathForKor);
            if(!dataFileForKor.exists()) {
                copyFilesForKor();
            }
        }
    }
    //[출처] [안드로이드 스튜디오] 안드로이드 OCR 앱 만들기|작성자 코스모스

    //copy file to device
    private void copyFiles() {
        try{
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFilesForKor() {
        try{
            String filepath = datapath + "/tessdata/kor.traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/kor.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Process an Image
    public String processImage(Bitmap bitmap) {
        Log.i("Tess-tow", "OCR processing start");
        long start = System.currentTimeMillis();
        mTess.setImage(bitmap);
        long end = System.currentTimeMillis();
        Log.i("Tess-tow", "OCR processing end. elapsed time : " + (end - start));
        return mTess.getUTF8Text();
    }
}

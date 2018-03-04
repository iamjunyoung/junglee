package com.bbeaggoo.junglee;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bbeaggoo.junglee.db.DbOpenHelper;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nilanchal
 *         <p/>
 *         Used to download the image, and after download completes,
 *         display it to imageView
 */
class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    //private final WeakReference<ImageView> imageViewReference;

    /*
    public ImageDownloaderTask(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }
    */
    Context context;
    private DbOpenHelper mDbOpenHelper;

    public ImageDownloaderTask() {

    }

    public ImageDownloaderTask(Context context) {

        this.context = context;
        mDbOpenHelper = new DbOpenHelper(context);
        mDbOpenHelper.open();
    }

    //doInBackground 메소드는 기존의 Thread 에서의 run() 메소드라고 보면 됨.
    @Override
    protected Bitmap doInBackground(String... params) {
        Log.i("ImageDownloaderTask", "doInBackground() start.");
        OpenGraphData data = getWebPageInfo(params[0]);
        Log.i("ImageDownloaderTask", "doInBackground() start 2.");
        Bitmap bitmap = downloadBitmap(data.getImage());
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        Log.i("ImageDownloaderTask", "doInBackground() start.3");

        //drawable을 파일로 저장한다
        String path = saveBitmapToJpeg(bitmap, "testmen", data.getTitle().toString());
        //이때 ArrayList mySaveList 뿐만 아니라 DB에도 저장해야 한다.
        //path로 insert하면 안되고 uri로 해야한다.
        Uri uri = getImageContentUri(context, new File(path));
        ListItem listItem = new ListItem(-1, data.getTitle(), data.getUrl(), data.getDescription(), d, uri, null, null, getCurrentTime());
        long id = mDbOpenHelper.insertColumn(data.getTitle(), data.getUrl(), data.getDescription(), uri.toString(), "", "", getCurrentTime(), "no", "no");
        listItem.setId(id);
        Log.i("ImageDownloaderTask", "insert to : " + id + "     title : " + data.getTitle() + "    url : " + data.getUrl());

        CilpboardListenerService.mySaveList.add(listItem);

        shareTo(data.getTitle(), data.getUrl());
        return bitmap;
    }

    public void shareTo(String title, String clipboardText) {
        Log.i("ImageDownloaderTask", "call shareTo");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.google.android.keep");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, clipboardText);

        //intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "해당 앱이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.i("ImageDownloaderTask", "onPostExecute() got listItem");
        //Add this item to list
        //CilpboardListenerService.mySaveList.add(listItem);

        /*
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    //여기서는 imageView에 바로 set하지 말고
                    //ArrayList<ListItem> 에 add를 한다.
                    //이후 사용자가 ListActivity를 load할 때
                    //ArrayList <--> CustomListAdapter 의 내용을 ListActivity에 뿌려주기만 하면 된다.
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
        */
    }

    private String saveBitmapToJpeg(Bitmap bitmap, String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i("JYN", "storage : " + ex_storage);
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder;
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;
        Log.i("JYN", "string_path : " + string_path);

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.exists()) {
                file_path.mkdir();
                Log.i("JYN", "make dir in " + file_path);
            }
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(string_path+"/"+file_name));

            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
        return string_path+"/"+file_name;
    }

    private Bitmap downloadBitmap(String url) {
        /*
        1. 타이틀
        2. url
        3. Description
        4. 사진(bitmap)
        5. Category defined by user(사용자가 정의한 카테고리, ex. 중고차)
        6. date
         */
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=4;

                return bitmap;
            }
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private OpenGraphData getWebPageInfo(String url) {
        final OpenGraph openGraph = new OpenGraph.Builder(url)
                .logger(new OpenGraph.Logger() {
                    @Override
                    public void log(String tag, String msg) {
                        Log.i("OpenGraph", msg);
                    }
                })
                .build();
        OpenGraphData data = null;
        try {
            data = openGraph.getOpenGraph();
        } catch (IOException e) {
            Log.i("ImageDownloaderTask", "Exception is occured when getWebPackageInfo()");
        }
        Log.i("ImageDownloaderTask", " data : " + data);
        if (data != null) {
            Log.i("ImageDownloaderTask", " data.getImage() : " + data.getImage());
        }
            //얘는 png, jpg로 정상적으로 나옴을 확인.
        //
        return data;
    }

    private String getCurrentTime() {
        // 시스템으로부터 현재시간(ms) 가져오기
        long now = System.currentTimeMillis();
        // Data 객체에 시간을 저장한다.
        Date date = new Date(now);
        // 각자 사용할 포맷을 정하고 문자열로 만든다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);
        return strNow;
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath },
                null);
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
}
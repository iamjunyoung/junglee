package com.bbeaggoo.junglee;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.bbeaggoo.junglee.screenshot.ScreenshotsLoadedActivity;
import com.bbeaggoo.junglee.screenshot.TessActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import com.example.trackingrunningappservice.IRunningProcessListService;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "APVM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
        boolean isServiceRunning = checkMyServiceIsRunning();

        if (!isServiceRunning) {
            //start ClipboardListenerService
            ComponentName compo2 = new ComponentName("com.bbeaggoo.junglee", "com.bbeaggoo.junglee.CilpboardListenerService");
            Intent intentForOb2 = new Intent();
            intentForOb2.setComponent(compo2);
            startService(intentForOb2);
            Log.i(TAG, "Start clipboard listener service");
        } else {
            //Don't start ClipboardListenerService
            Log.i(TAG, "Skip Start clipboard listener service");
        }
        */

        Log.i(TAG, "MainActivity is onCreate()");
    }

    private boolean checkMyServiceIsRunning() {
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(100);

        String processName = null;
        for (int i=0; i<rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            processName = rsi.process;
            Log.i(TAG, "" + rsi.toString() + "    Process " + processName + " with component " + rsi.service.getClassName());
            if (processName != null && processName.contains("junglee")) {
                return true;
            }
        }
        return false;
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        ComponentName compo = new ComponentName("com.bbeaggoo.junglee", "com.bbeaggoo.junglee.NewService");
        Intent intentForOb = new  Intent();
        ComponentName compo2 = new ComponentName("com.bbeaggoo.junglee", "com.bbeaggoo.junglee.CilpboardListenerService");
        Intent intentForOb2 = new Intent();

        //ComponentName compoForOverlayService = new ComponentName("com.bbeaggoo.junglee", "com.bbeaggoo.junglee.overlayview.AlwaysTopServiceTouch");
        // Intent intentForOverlay = new Intent();

        switch (v.getId()) {
            case R.id.clipboard:
                intentForOb2.setComponent(compo2);
                startService(intentForOb2);

                intentForOb.setComponent(compo);
                startService(intentForOb);

                //intentForOverlay.setComponent(compoForOverlayService);
                //startService(new Intent(intentForOverlay));
                Log.i(TAG, "Start ClipboardListener service and ContentObserver service");
                break;
            case R.id.clipboardend:
                intentForOb2.setComponent(compo2);
                stopService(intentForOb2);

                intentForOb.setComponent(compo);
                stopService(intentForOb);

                //stopService(new Intent(this, AlwaysTopServiceTouch.class));
                Log.i(TAG, "End Clipboard Listener service and ContentObserver Service");
                break;
            case R.id.myList:
                Intent intent = new Intent(this, MyListActivity.class);
                startActivity(intent);
                Log.i(TAG, "StartActivity MyListActivity");
                break;
            case R.id.myListReal:
                Intent intentReal = new Intent(this, ListActivity.class);
                startActivity(intentReal);
                Log.i(TAG, "StartActivity ListActivity Real");
                break;
            case R.id.grid:
                Intent grid = new Intent(this, GridActivity.class);
                startActivity(grid);
                Log.i(TAG, "StartActivity GridActivity");
                break;
            case R.id.getAllImages:
                Log.i(TAG, "Get all images");
                List<Uri> uriList = fetchAllImages();
                //Log.i(TAG, "" + uriList);
                break;
            case R.id.expandable:
                Intent expandableIntent = new Intent(this, ExpandableDrawerActivity.class);
                startActivity(expandableIntent);
                break;
            case R.id.getAllClipboard:
                Log.v(TAG, "Get All Clipboard");

                final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                int size = clipBoard.getPrimaryClip().getItemCount();
                for (int i = 0 ; i < size ; i++) {
                    Log.i(TAG, "  " + i + " : " + clipBoard.getPrimaryClip().getItemAt((i)));
                }
                break;
            case R.id.db:
                Intent menuDb = new Intent(this, com.bbeaggoo.junglee.menudb.ContactMain.class);
                startActivity(menuDb);
                break;
            //
            case R.id.editcategory:
                Intent CategoryEditActivity = new Intent(this, com.bbeaggoo.junglee.category.CategoryEditActivity.class);
                startActivity(CategoryEditActivity);
                break;
            case R.id.tesseract:
                Intent tessActivity = new Intent(this, TessActivity.class);
                startActivity(tessActivity);
                Log.i(TAG, "StartActivity tessActivity ");
                break;
            case R.id.screenshotImageProcessing:
                Intent screenshotsLoadedActivity = new Intent(this, ScreenshotsLoadedActivity.class);
                startActivity(screenshotsLoadedActivity);
                Log.i(TAG, "StartActivity ScreenshotsLoadedActivity ");
                break;
            case R.id.start_always_on_top_service:
                Intent AOTS = new Intent(this, AlwaysOnTopService.class);
                startService(AOTS);
                break;
            case R.id.stop_always_on_top_service:
                Intent AOTS_for_stop = new Intent(this, AlwaysOnTopService.class);
                stopService(AOTS_for_stop);
                break;
            case R.id.file_explore:
                Intent fileExploreActivityIntent = new Intent(this, FileExplorer.class);
                startActivity(fileExploreActivityIntent);
                break;
            case R.id.file_explorer_for_images:
                Intent fileExploreForImagesActivityIntent = new Intent(this, FileExplorerForImages.class);
                startActivity(fileExploreForImagesActivityIntent);
        }
    }

    /*
    private void addOGTypeMemo(String url, final OGTag ret) {
        // 입력받은 url에 해당하는 html을 요청한다.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(DownloadManager.Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // html에서 OGTag들을 가져온다.
                        DocumentsContract.Document doc = Jsoup.parse(response);
                        Elements ogTags = doc.select("meta[property^=og:]");
                        if (ogTags.size() <= 0) {
                            return;
                        }

                        // 필요한 OGTag를 추려낸다
                        for (int i = 0; i < ogTags.size(); i++) {
                            Element tag = ogTags.get(i);

                            String text = tag.attr("property");
                            if ("og:url".equals(text)) {
                                ret.setOgUrl(tag.attr("content"));
                            } else if ("og:image".equals(text)) {
                                ret.setOgImageUrl(tag.attr("content"));
                            } else if ("og:description".equals(text)) {
                                ret.setOgDescription(tag.attr("content"));
                            } else if ("og:title".equals(text)) {
                                ret.setOgTitle(tag.attr("content"));
                            }
                        }

                        // 필요한 작업을 한다.
                        doSomething(ret);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        queue.add(stringRequest);
    }
    */

    List<Uri> fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED};

        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int dataColumnIndex2 = imageCursor.getColumnIndex(projection[1]);
        int dataColumnIndex3 = imageCursor.getColumnIndex(projection[2]);

        Log.i(TAG, "dataColumnIndex : " + dataColumnIndex);

        if (imageCursor == null) {
            // Error 발생
            // 적절하게 handling 해주세요
        } else if (imageCursor.moveToFirst()) {
            int i = 0;
            do {
                String filePath = imageCursor.getString(dataColumnIndex);
                Uri imageUri = Uri.parse(filePath);
                result.add(imageUri);

                String fileSize = imageCursor.getString(dataColumnIndex2);
                String fileAdded = imageCursor.getString(dataColumnIndex3);
                //if (filePath != null && filePath.contains("Screenshots")) {
                File f = new File(filePath);
                Log.i(TAG, "[ " + ++i + " ]  " + imageUri + "  size : " + fileSize +
                        "  type : " + getMimeType(filePath) +
                        "  parent : " + f.getParentFile().getName() + "  parent all :" + f.getParent().toString());
                //}
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();
        return result;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
        // https://code.i-harness.com/ko/q/83114d
        // http://susemi99.tistory.com/896
    }

    List<Uri> fetchScreenShotImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

        if (imageCursor == null) {
            // Error 발생
            // 적절하게 handling 해주세요
        } else if (imageCursor.moveToFirst()) {
            int i = 0;
            do {
                String filePath = imageCursor.getString(dataColumnIndex);
                Uri imageUri = Uri.parse(filePath);
                result.add(imageUri);
                Log.i(TAG, "[ " + ++i + " ]  " + imageUri);
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();
        return result;
    }



    Uri uriToThumbnail(String imageId) {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = { MediaStore.Images.Thumbnails.DATA };
        ContentResolver contentResolver = getContentResolver();

        // 원본 이미지의 _ID가 매개변수 imageId인 썸네일을 출력
        Cursor thumbnailCursor = contentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, // 썸네일 컨텐트 테이블
                projection, // DATA를 출력
                MediaStore.Images.Thumbnails.IMAGE_ID + "=?", // IMAGE_ID는 원본 이미지의 _ID를 나타냅니다.
                new String[]{imageId},
                null);
        if (thumbnailCursor == null) {
            // Error 발생
            // 적절하게 handling 해주세요
            return null;
        } else if (thumbnailCursor.moveToFirst()) {
            int thumbnailColumnIndex = thumbnailCursor.getColumnIndex(projection[0]);

            String thumbnailPath = thumbnailCursor.getString(thumbnailColumnIndex);
            thumbnailCursor.close();
            return Uri.parse(thumbnailPath);
        } else {
            // thumbnailCursor가 비었습니다.
            // 이는 이미지 파일이 있더라도 썸네일이 존재하지 않을 수 있기 때문입니다.
            // 보통 이미지가 생성된 지 얼마 되지 않았을 때 그렇습니다.
            // 썸네일이 존재하지 않을 때에는 아래와 같이 썸네일을 생성하도록 요청합니다
            MediaStore.Images.Thumbnails.getThumbnail(contentResolver, Long.parseLong(imageId), MediaStore.Images.Thumbnails.MINI_KIND, null);
            thumbnailCursor.close();
            return uriToThumbnail(imageId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflater함수를 이용해서 menu 리소스를 menu로 변환.
        // 한 줄 코드
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

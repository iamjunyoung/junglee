package com.bbeaggoo.junglee.screenshot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bbeaggoo.junglee.R;

import java.io.File;
import java.util.ArrayList;

public class ScreenshotsLoadedActivity extends AppCompatActivity {

    private ScreenshotAdaptor adapter;

    private Context mContext;
    private RecyclerView recyclerView;
    private static String TAG = "JY_S";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots_loaded);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        mContext = this;

        ArrayList<ScreenshotItem> screenshotItemList = new ArrayList<>();
        ArrayList<Uri> arrayList = fetchAllImages();
        if (arrayList != null) {
            for (Uri uri : arrayList) {
                ScreenshotItem sItem = new ScreenshotItem();
                sItem.setImg(uri);
                Log.i(TAG, "set uri to list : " + uri);
                sItem.setExtractedText("Temporary");
                screenshotItemList.add(sItem);
            }
        }
// I/CustomGridAdapter: Title : [명대사 한줄] 자신의 행복을 바라는 마음은 동전의 양면 같아
// uri : content://media/external/images/media/109199


        adapter = new ScreenshotAdaptor(this, screenshotItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new ItemLayoutManger(this));

    }

    ArrayList<Uri> fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
        };

        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int dataColumnIndexForID = imageCursor.getColumnIndex(projection[1]);
        //int dataColumnIndex3 = imageCursor.getColumnIndex(projection[2]);

        Log.i(TAG, "dataColumnIndex : " + dataColumnIndex);

        if (imageCursor == null) {
            // Error 발생
            // 적절하게 handling 해주세요
        } else if (imageCursor.moveToFirst()) {
            int i = 0;
            do {
                String filePath = imageCursor.getString(dataColumnIndex);
                Uri imageUri = Uri.parse(filePath);
                //result.add(imageUri); 여기서 조건없이 add하는 것이 아니라 아래 조건까지 체크한 다음에 아래 조건에 맞으면 add 하자

                //String fileSize = imageCursor.getString(dataColumnIndex2);
                //String fileAdded = imageCursor.getString(dataColumnIndex3);
                if (filePath != null && filePath.contains("Screenshots")) {
                    int id = imageCursor.getInt(dataColumnIndexForID);
                    Uri baseUri = Uri.parse("content://media/external/images/media");
                    Uri realUri = Uri.withAppendedPath(baseUri, "" + id);
                    result.add(realUri);
                    Log.i(TAG, "[ " + ++i + " ]  filePath : "+ filePath + "  uri : " + imageUri + "    real uri : " + realUri);
                }
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();
        return result;
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
}

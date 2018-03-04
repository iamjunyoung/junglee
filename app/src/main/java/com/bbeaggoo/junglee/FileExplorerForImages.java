package com.bbeaggoo.junglee;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by junyoung on 2018. 2. 5..
 */


public class FileExplorerForImages extends ListActivity {
    private List<String> items = null;
    private List<String> paths = null;
    private String root = "/";
    private TextView mPath;
    private View view;
    ImageView imageView;
    private static String TAG = "FileExplorerForImages";

    LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer_list);
        mPath = (TextView) findViewById(R.id.path);
        //getDir(root);

        getDirForInitial();

        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void getDirForInitial() {
        HashMap<String, String> hm = fetchAllImages();
        paths = new ArrayList<String>();

        paths.addAll(hm.values());

        //list에 보여지는 애가 items
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row_for_file_explore, new ArrayList<String>(hm.keySet()));
        setListAdapter(fileList);
    }

    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            items.add(root);
            paths.add(root);
            items.add("../");
            paths.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            paths.add(file.getPath());
            Log.i(TAG, file.getPath());
            if (file.isDirectory())
                items.add(file.getName() + "/");
            else
                items.add(file.getName());
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row_for_file_explore, items);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        File file = new File(paths.get(position));
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(paths.get(position));
            else {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).show();
            }
        } else {
            Drawable d = null;
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), getImageContentUri(this, file));
                d = new BitmapDrawable(getResources(), bitmapImage);
            } catch (IOException e) {
                Log.i(TAG, "[FileExplorer] IOException occurred!!!");
            }

            view = inflater.inflate(R.layout.layout_for_dialog, null);
            imageView = (ImageView) view.findViewById(R.id.bigImage);

            if (d == null) d = getResources().getDrawable(R.drawable.ic_launcher, null);
            imageView.setImageDrawable(d);
            //아래 처럼 bitmap으로 해도 좋을듯...
            //imageView.setImageBitmap(test);

            new AlertDialog.Builder(this)
                    .setIcon(d)
                    .setTitle("[" + file.getName() + "]")
                    .setMessage("File path : " + file.getPath() + "\n"
                            + "Mime type : " + getMimeType(file.getPath()))
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).show();
        }
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

    /*
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.TITLE,
    MediaStore.Images.Media.DATA,
    MediaStore.Images.Media.SIZE,
    MediaStore.Images.Media.DATE_ADDED,
    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    MediaStore.Images.Media.BUCKET_ID,
    MediaStore.Images.Media.CONTENT_TYPE,
    MediaStore.Images.Media.DATE_MODIFIED,
    MediaStore.Images.Media.DATE_TAKEN,
    MediaStore.Images.Media.DESCRIPTION,
    MediaStore.Images.Media.MIME_TYPE,
    MediaStore.Images.Media.MINI_THUMB_MAGIC};
    */

    //List<Uri> fetchAllImages() {
    HashMap<String, String> fetchAllImages() {
        // DATA는 이미지 파일의 스트림 데이터 경로를 나타냅니다.
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media._ID,
                //MediaStore.Images.Media.CONTENT_TYPE,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.TITLE};

        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null);      // 정렬 안 함

        //ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
        HashMap<String, String> result = new HashMap<>();
        int dataColumn_DATA = imageCursor.getColumnIndex(projection[0]);
        int dataColumn_SIZE = imageCursor.getColumnIndex(projection[1]);
        int dataColumn_DATE_ADDED = imageCursor.getColumnIndex(projection[2]);
        int dataColumn_DATE_TAKEN = imageCursor.getColumnIndex(projection[3]);
        int dataColumn_DATE_MODIFIED = imageCursor.getColumnIndex(projection[4]);
        int dataColumn_BUCKET_DISPLAY_NAME = imageCursor.getColumnIndex(projection[5]);
        int dataColumn_BUCKET_ID = imageCursor.getColumnIndex(projection[6]);
        int dataColumn_ID = imageCursor.getColumnIndex(projection[7]);
        //int dataColumn_CONTENT_TYPE = imageCursor.getColumnIndex(projection[8]);
        int dataColumn_DESCRIPTION = imageCursor.getColumnIndex(projection[8]);
        int dataColumn_MIME_TYPE = imageCursor.getColumnIndex(projection[9]);
        int dataColumn_MINI_THUMB_MAGIC = imageCursor.getColumnIndex(projection[10]);
        int dataColumn_TITLE = imageCursor.getColumnIndex(projection[11]);

        if (imageCursor == null) {
            // Error 발생
            // 적절하게 handling 해주세요
        } else if (imageCursor.moveToFirst()) {
            int i = 0;
            do {
                String filePath = imageCursor.getString(dataColumn_DATA);
                Uri imageUri = Uri.parse(filePath);
                //result.add(imageUri);
                String fileSize = imageCursor.getString(dataColumn_SIZE);
                String DATE_ADDED = imageCursor.getString(dataColumn_DATE_ADDED);
                String DATE_TAKEN = imageCursor.getString(dataColumn_DATE_TAKEN);
                String DATE_MODIFIED = imageCursor.getString(dataColumn_DATE_MODIFIED);
                String BUCKET_DISPLAY_NAME = imageCursor.getString(dataColumn_BUCKET_DISPLAY_NAME);
                String BUCKET_ID = imageCursor.getString(dataColumn_BUCKET_ID);
                String ID = imageCursor.getString(dataColumn_ID);
                //String CONTENT_TYPE = imageCursor.getString(dataColumn_CONTENT_TYPE);
                String DESCRIPTION = imageCursor.getString(dataColumn_DESCRIPTION);
                String MIME_TYPE = imageCursor.getString(dataColumn_MIME_TYPE);
                String MINI_THUMB_MAGIC = imageCursor.getString(dataColumn_MINI_THUMB_MAGIC);
                String TITLE = imageCursor.getString(dataColumn_TITLE);

                //if (filePath != null && filePath.contains("Screenshots")) {
                File f = new File(filePath);
                Log.i(TAG, "[ " + ++i + " ]  " + imageUri + "  size : " + fileSize +
                        "  type : " + getMimeType(filePath) +
                        "  parent(k) : " + f.getParentFile().getName() + "  parent all(v) :" + f.getParent().toString());
                Log.i(TAG, "ADDED : " + DATE_ADDED + "  TAKEN : " + DATE_TAKEN + "  MODIFY : " + DATE_MODIFIED
                        + "  BUCKET : " + BUCKET_DISPLAY_NAME + "  B_ID : " + BUCKET_ID + "  ID : " + ID
                        + "  DESC : " + DESCRIPTION + "  M_TYPE : " + MIME_TYPE
                        + "  MINI_THUMB : " + MINI_THUMB_MAGIC + "  TITLE : " + TITLE);
                result.put(f.getParentFile().getName(), f.getParent().toString());
                //}
            } while(imageCursor.moveToNext());
        } else {
            // imageCursor가 비었습니다.
        }
        imageCursor.close();
        return result;
    }

    public Drawable loadImageWithGlide() {
        //Glide.with(this).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView); // OOM 없애기위해 그레들사용
        return null;
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
}
//출처: http://mainia.tistory.com/1188 [녹두장군 - 상상을 현실로]
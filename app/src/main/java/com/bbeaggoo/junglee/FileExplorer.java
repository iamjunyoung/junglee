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
import java.util.List;

/**
 * Created by junyoung on 2018. 2. 5..
 */


public class FileExplorer extends ListActivity {
    private List<String> item = null;
    private List<String> path = null;
    private String root = "/";
    private TextView mPath;
    private View view;
    ImageView imageView;

    LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer_list);
        mPath = (TextView) findViewById(R.id.path);
        getDir(root);


        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());
            Log.i("FileExplorer", file.getPath());
            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row_for_file_explore, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        File file = new File(path.get(position));
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position));
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
                Log.i("JYN", "[FileExplorer] IOException occurred!!!");
            }

            view = inflater.inflate(R.layout.layout_for_dialog, null);
            imageView = (ImageView) view.findViewById(R.id.bigImage);

            if (d == null) d = getResources().getDrawable(R.drawable.ic_launcher, null);
            imageView.setImageDrawable(d);

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
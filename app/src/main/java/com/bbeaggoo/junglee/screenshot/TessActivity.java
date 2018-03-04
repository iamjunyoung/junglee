package com.bbeaggoo.junglee.screenshot;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbeaggoo.junglee.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TessActivity extends AppCompatActivity {

    static int GET_PICTURE_URI = 1;

    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름
    ImageView imageView = null;


    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로
    //[출처] [안드로이드 스튜디오] 안드로이드 OCR 앱 만들기|작성자 코스모스


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tess);

        imageView = (ImageView) findViewById(R.id.imageView) ;

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


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_photo_button:
                selectPhoto();
                break;
            //case R.id.run_ocr:

            //    break;
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        //intent.setType(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        //intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


        startActivityForResult(intent, GET_PICTURE_URI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_URI) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //String bucket = MediaStore.Images.Media.get
                    Log.i("JYN", "set image bitmap : " + image + "    data : " + data.getData());
//                    배치해놓은 ImageView에 이미지를 넣어봅시다.

                    //imageView.setImageBitmap(bitmap);
                    Glide.with(this).load(data.getData()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView); // OOM 없애기위해 그레들사용

                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
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
    public void processImage(View view) {
        String OCRresult = null;
        //이미지 디코딩을 위한 초기화
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.sample4); //샘플이미지파일

        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = (TextView) findViewById(R.id.ocr_result_textView);
        OCRTextView.setText(OCRresult);
    }

}

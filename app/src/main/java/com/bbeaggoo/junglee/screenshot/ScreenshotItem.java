package com.bbeaggoo.junglee.screenshot;

import android.net.Uri;

/**
 * Created by junyoung on 2017. 12. 30..
 */

public class ScreenshotItem {

    private Uri img;
    private String extractedText;

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}

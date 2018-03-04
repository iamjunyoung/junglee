package com.bbeaggoo.junglee;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ListItem {

    private long id;
    private String title;
    private String url = null;
    private String desc;
    private Drawable img;
    private Uri uri = null;
    private String category;
    private String folder;
    private String date;
    private boolean checked;

    /*
            1. 타이틀
            2. url
            3. Description
            4. 사진
            5. Category defined by user(사용자가 정의한 카테고리, ex. 중고차)
            6. date
     */

    public ListItem() {
    }

    public ListItem(long id, String title, String url, String desc, Drawable img, Uri uri,
                    String category, String folder, String date) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.img = img;
        this.uri = uri;
        this.category = category;
        this.folder = folder;
        this.date = date;
    }

    public boolean getChecked() { return checked; }

    public void setChecked(boolean checked) { this.checked = checked; }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public Uri getUri() { return uri; }

    public void setUri(Uri uri) { this.uri = uri; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFolder() { return folder; }

    public void setFolder(String folder) { this.folder = folder; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
	public String toString() {
		return "Title : " + getTitle();
	}
}

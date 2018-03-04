package com.bbeaggoo.junglee.category;

import java.util.List;

/**
 * Created by junyoung on 17. 1. 2..
 */
public class Category {
    private String categoryName = null;
    private List<Folder> folderList = null;
    private int currentFolderSize = 0;
    private String date = null;

    public Category(String categoryName, String date) {
        this.categoryName = categoryName;
        this.date = date;
    }

    public Category(String categoryName, List<Folder> folderList, String date) {
        this.categoryName = categoryName;
        this.folderList = folderList;
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Folder> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<Folder> folderList) {
        this.folderList = folderList;
    }

    public int getCurrentFolderSize() { return currentFolderSize; }

    public void setCurrentFolderSize(int currentFolderSize) { this.currentFolderSize = currentFolderSize; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

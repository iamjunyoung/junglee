package com.bbeaggoo.junglee.db;

import android.provider.BaseColumns;

/**
 * Created by junyoung on 16. 10. 23..
 */
// DataBase Table
public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        public static final String TITLE = "title";
        public static final String URL = "url";
        public static final String DESC = "desc";
        public static final String IMAGE =  "image";
        public static final String CATEGORY = "category";
        public static final String FOLDER = "folder";
        public static final String DATE = "date";
        public static final String _TABLENAME = "mytable";
        public static final String ISCATEGORY = "iscategory";
        public static final String ISFOLDER = "isfolder";
        public static final String _CREATE =
                "create table "+_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +TITLE+" text , "
                        +URL+" text , "
                        +DESC+" text , "
                        +IMAGE+" text , "
                        +CATEGORY+" text , "
                        +FOLDER+" text , "
                        +DATE+" text not null ,"
                        +ISCATEGORY+" text not null ,"
                        +ISFOLDER+" text not null)";
    }
}
/*
        1. 타이틀
        2. url
        3. Description
        4. 사진
        5. Category defined by user(사용자가 정의한 카테고리, ex. 중고차)
        6. date
 */

package com.bbeaggoo.junglee.category.model;

import java.util.ArrayList;

/**
 * Created by wlsdud.choi on 2016-04-08.
 */
public class ParentItem extends Item {

    public boolean visibilityOfChildItems = true;
    public ArrayList<ChildItem> unvisibleChildItems = new ArrayList<>();

    //여기에 child 정보를 알 수 있는 ArrayList를 추가해야 할듯.
    public ParentItem(String name, int viewType){
        this.name = name;
        this.viewType = viewType;
    }

}

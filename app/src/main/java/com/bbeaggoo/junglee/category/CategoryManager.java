package com.bbeaggoo.junglee.category;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bbeaggoo.junglee.CustomGridAdapter;
import com.bbeaggoo.junglee.ExpandableDrawerAdapter;
import com.bbeaggoo.junglee.ListItem;
import com.bbeaggoo.junglee.R;
import com.bbeaggoo.junglee.db.DataBases;
import com.bbeaggoo.junglee.db.DbOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by junyoung on 17. 1. 2..
 */
public class CategoryManager {

    private Context mContext;
    public DrawerLayout dl;
    public ExpandableListView xl;
    //public ActionBarDrawerToggle adt;
    public List<String> alkitab;
    public HashMap<String, List<String>> data_alkitab;
    public CharSequence title;
    private int lastExpandPosition = -1;
    private MenuItem menuItem;
    private ExpandableDrawerAdapter adapt;
    private DbOpenHelper mDbOpenHelper;

    private static CategoryManager cmInstance;

    private int longClickedCategoryPos;

    ListItem mListItem = null;
    CustomGridAdapter mCustomGridAdapter = null;

    AlertDialog.Builder listViewDialog;
    AlertDialog alertDialogForListView;

    public CategoryManager(Context context, DrawerLayout dl, ExpandableListView xl) {
        mContext = context;
        this.dl = dl;
        this.xl = xl;

        Log.i("JYN", "CategoryManager is generated (with params 3)");
        defauiltSetting();
        //CategoryManager 생성시 DB load
        loadCategoryFromDB();
    }

    public CategoryManager(Context context, ExpandableListView xl) {
        mContext = context;
        this.xl = xl;

        Log.i("JYN", "CategoryManager is generated (with params 2)");
        defauiltSetting();
        //CategoryManager 생성시 DB load
        loadCategoryFromDB();
    }

    public CategoryManager(Context context) {
        mContext = context;
        Log.i("JYN", "CategoryManager is generated (default)");
        defauiltSetting();
        loadCategoryFromDB();
    }

    public CategoryManager(DrawerLayout dl, ExpandableListView xl, List<String> alkitab, HashMap<String, List<String>> data_alkitab, CharSequence title, int lastExpandPosition, MenuItem menuItem, ExpandableDrawerAdapter adapt) {
        this.dl = dl;
        this.xl = xl;
        this.alkitab = alkitab;
        this.data_alkitab = data_alkitab;
        this.title = title;
        this.lastExpandPosition = lastExpandPosition;
        this.menuItem = menuItem;
        this.adapt = adapt;
    }

    public void defauiltSetting() {
        alkitab = new ArrayList<String>();
        data_alkitab = new HashMap<String, List<String>>();

        loadData();
        //getExpandableListView().setGroupIndicator(null);
        Log.i("JYN", "xl in GridActivity : " + xl);
        adapt = new ExpandableDrawerAdapter(mContext, alkitab, data_alkitab, xl);

        if (xl != null) {
            xl.setAdapter(adapt);
            xl.setTextFilterEnabled(true);
            xl.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    Log.i("JYN", "onGroupClick : " + groupPosition + "    name : " + getGroup(groupPosition).toString());
                    if (mListItem != null) {
                        if (groupPosition != alkitab.size() - 1) {
                            //set category DB
                            String setCategoryName = getGroup(groupPosition).toString();
                            setCategoryOfItem(setCategoryName, mListItem);

                            //update UI
                            //reload하는게 필요할듯?? 그 이후에 notifyDataSetChanged()를 해야 효과가 있지..
                            mListItem.setCategory(setCategoryName);
                            Log.i("JYN", "Before set category : " + mListItem.getCategory());
                            mCustomGridAdapter.updateListData(mListItem);
                            mCustomGridAdapter.notifyDataSetChanged();
                        }
                    }

                        //If last one is clicked, handle this.
                    if (groupPosition == alkitab.size() - 1) {
                        Toast.makeText(mContext, "Last group", Toast.LENGTH_SHORT).show();

                        //add category 하지 말고 설정할수 있는 activity인 CategoryEditActivity를 띄운다
                        //addCategory();
                        if (alertDialogForListView != null) {
                            alertDialogForListView.dismiss();
                        }
                        Intent intent = new Intent(mContext, com.bbeaggoo.junglee.category.CategoryEditActivity.class);
                        mContext.startActivity(intent);
                    }
                    return true; //  true일 경우 expand하지 않고 반응없음
                    //return false;
                }
            });

            xl.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("JYN", "long clicked pos : " + position + "    parent : " + parent + "    view : " + view);

                    int itemType = ExpandableListView.getPackedPositionType(id);
                    boolean retVal = true;

                    if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                        int childPosition = ExpandableListView.getPackedPositionChild(id);
                        int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                        Log.d("JYN", "PACKED_POSITION_TYPE_GROUP : " + groupPosition);
                        Log.d("JYN", "PACKED_POSITION_TYPE_CHILD : " + childPosition);
                        //do your per-item callback here
                        return retVal; //true if we consumed the click, false if not

                    } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                        int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                        Log.d("JYN", "PACKED_POSITION_TYPE_GROUP : " + groupPosition);

                        //do your per-group callback here
                        return retVal; //true if we consumed the click, false if not

                    } else {
                        // null item; we don't consume the click
                        return false;
                    }
                } //End Of Method onItemLongClick
            });


            xl.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (lastExpandPosition != -1 && groupPosition != lastExpandPosition) {
                        xl.collapseGroup(lastExpandPosition);
                    }
                    lastExpandPosition = groupPosition;
                }
            });

            xl.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                }
            });

            xl.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    int grup_pos = (int) adapt.getGroupId(groupPosition);
                    Log.i("JYN", "grup_pos : " + grup_pos + "    groupPosition : " + groupPosition + "    child_pos : " + childPosition);
                    int child_pos = (int) adapt.getChildId(groupPosition, childPosition);
                    if (grup_pos == 1) {
                        switch (child_pos) {
                            case 0:
                                Toast.makeText(mContext, "Child 1 Group 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(mContext, "Child 2 Group 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(mContext, "Child 3 Group 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(mContext, "Child 4 Group 1", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });

        }
    }

    public Object getGroup(int groupPosition) {
        return this.alkitab.get(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return this.data_alkitab.get(this.alkitab.get(groupPosition)).get(childPosition);
    }

    public void showDialogForCategoryList(Context context) {
        showDialogForCategoryList(context, null);
    }

    public void showDialogForCategoryList(Context context, ListItem item) {
        showDialogForCategoryList(context, item, null);
    }

    public void showDialogForCategoryList(Context context, ListItem item, CustomGridAdapter customGridAdapter) {
        Log.i("JYN", "showDialogForCategoryList()");
        mContext = context;
        mListItem = item;
        mCustomGridAdapter = customGridAdapter;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_for_popup, null);
        xl = (ExpandableListView)view.findViewById(R.id.left_drawer);
        //CategoryManager categoryManager = new CategoryManager(mContext, dl, xl);
        defauiltSetting();
        //CategoryManager 생성시 DB load
        loadCategoryFromDB();

        listViewDialog = new AlertDialog.Builder(mContext);
        // 리스트뷰 설정된 레이아웃
        listViewDialog.setView(view);
        // 확인버튼
        listViewDialog.setPositiveButton("확인", null);
        // 아이콘 설정
        listViewDialog.setIcon(R.drawable.ic_launcher);
        // 타이틀
        listViewDialog.setTitle("ListView DiaLog");

        // 다이얼로그 보기
        alertDialogForListView = listViewDialog.show();
    }

    public void loadData(){
        //alkitab 는 List<String> 이고 각 String은 Parent임.
        /*
        alkitab.add("Group 1");
        alkitab.add("Group 2");
        alkitab.add("Group 3");
        alkitab.add("Group 4");
        */
        alkitab.add("카테고리 추가"); //Folder객체를 생성하고 Foder객체 생성자 안에서 alkitab.add 를 해주는건 어떨까?
        //그리고 초기 생성시에는 child가 0 - 이에 맞는 inicator 이미지 set (NONE?)
        //long touch event 달기 - child 추가 메뉴 보여주기

        /*
        List<String> kitab_perjanjian_lama = new ArrayList<String>();
        kitab_perjanjian_lama.add("Child 1 Of Group 1");
        kitab_perjanjian_lama.add("Child 2 Of Group 1");

        List<String> kitab_perjanjian_baru = new ArrayList<String>();
        kitab_perjanjian_baru.add("Child 1 Of Group 2");
        kitab_perjanjian_baru.add("Child 2 Of Group 2");

        List<String> kidung_jemaat = new ArrayList<String>();
        kidung_jemaat.add("Child 1 Of Group 3");
        kidung_jemaat.add("Child 2 Of Group 3");

        List<String> gita_bakti = new ArrayList<String>();
        gita_bakti.add("Child 1 Of Group 4");
        gita_bakti.add("Child 2 Of Group 4");
        */
        List<String> add_category_item = new ArrayList<String>();
        /*
        data_alkitab.put(alkitab.get(0), kitab_perjanjian_lama);
        //data_alkitab.put(alkitab.get(0), null);
        data_alkitab.put(alkitab.get(1), kitab_perjanjian_baru);
        data_alkitab.put(alkitab.get(2), kidung_jemaat);
        data_alkitab.put(alkitab.get(3), gita_bakti);
        */
        data_alkitab.put(alkitab.get(alkitab.size()-1), add_category_item);
    }

    public void addAndReCreateCategoryMenu(String addedCategory) {
        Log.i("JYN", "[addAndReCreateCategoryMenu] addedCategory : " + addedCategory);
        //아래의 hashMap관련 로직을 수행하지 말고 insertColumn후에 그냥 loadCategoryFromDB() 를 해서 re-load를 해버리는 건 어떨까?
        addCategoryToListAndHashMap(addedCategory);
        long id = mDbOpenHelper.insertColumn("", "", "", "", addedCategory, "", getCurrentTime(), "yes", "no");
        Log.i("CategoryManager", "insert new category : " + addedCategory + "  to list and DB. Id : " + id);
    }

    //parameter로 position 또는 category정보가 필요할듯..
    public void addAndReCreateFolderMenu(String addedFolder, String addedToCategory) {
        Log.i("JYN", "[addAndReCreateFolderMenu] addedFolder : " + addedFolder);

        //아래의 hashMap관련 로직을 수행하지 말고 insertColumn후에 그냥 loadCategoryFromDB() 를 해서 re-load를 해버리는 건 어떨까?
        addFolderToHashMap(addedToCategory, addedFolder);
        long id;
        id = mDbOpenHelper.insertColumn("", "", "", "", addedToCategory, addedFolder, getCurrentTime(), "no", "yes");
        Log.i("CategoryManager", "insert new folder : " + addedFolder + "  to list and DB. Id : " + id);
    }

    public void addCategoryToListAndHashMap(String addedCategory) {
        alkitab.add(alkitab.size()-1, addedCategory);
        List<String> test = new ArrayList<String>();
        //Log.i("JYN", "put : " + addedCategory);
        data_alkitab.remove(alkitab.get(alkitab.size()-1));
        data_alkitab.put(addedCategory, test);

        List<String> add_category_item = new ArrayList<String>();
        data_alkitab.put(alkitab.get(alkitab.size()-1), add_category_item);
        adapt.notifyDataSetChanged();
    }

    public void addFolderToHashMap(String categoryName, String folderName) {
        //해당 Folder가 속할 Category를 알고 있어야 함
        //그래야 data_alkitab HashMap에서 key값을 갖고와서 value(List)에 해당 folder를 add해 줄수 있게 됨.

            List<String> folderAddedList = data_alkitab.get(categoryName);
            folderAddedList.add(folderName);
            data_alkitab.put(categoryName, folderAddedList);
            //        Log.i("JYN", "" + headerTitle + "    " + _data_alkitab.get(getGroup(groupPosition)).size());

            //Log.i("JYN" ,"addFolderToHashMap() " + folderName + "    hashMap size : " + data_alkitab.size());
            //For debugging
            /*
            for (int i = 0 ; i < alkitab.size() ; i++) {
                for (int j = 0; j < data_alkitab.get(alkitab.get(i)).size(); j++) {
                    Log.i("JYN", "category " + alkitab.get(i) + " i : " + i + "    j : " + j + "    " + data_alkitab.get(alkitab.get(i)).get(j));
                }
            }
            */
            //Log.i("JYN", "addFolderToHashMap() addedToCategory : " + categoryName);
    }

    public void setCategoryOfItem(String settingCategoryName, ListItem listItem) { mDbOpenHelper.setCategoryColumn(listItem.getUrl(), settingCategoryName);}

    public void deleteCategory(String categoryName) {
        mDbOpenHelper.deleteCategory(categoryName);
    }

    public void deleteFolder(String folderName) {
        mDbOpenHelper.deleteFolder(folderName);
    }

    public void updateCategoryColumn(String origCategoryName, String newCategoryName) { mDbOpenHelper.updateCategoryColumn(origCategoryName, newCategoryName);}

    public void updateFolderColumn(String origFolderName, String newFolderName) { mDbOpenHelper.updateFolderColumn(origFolderName, newFolderName);}

    public void loadCategoryFromDB() {
        Log.i("JYN", "loadCategoryFromDB()");
        mDbOpenHelper = new DbOpenHelper(mContext);
        mDbOpenHelper.open();
        Cursor mCursor = mDbOpenHelper.getColumsForCategory();

        while (mCursor.moveToNext()) {
            String isCategory = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.ISCATEGORY));
            String isFolder = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.ISFOLDER));
            String category = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.CATEGORY));
            String folder = mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FOLDER));
            Log.i("JYN", "isCategory : " + isCategory +
                    "    isFolder : " + isFolder +
                    "    category : " + category +
                    "    folder : " + folder);

            if ("yes".equals(isCategory)) {
                //이 경우 category에 해당한다.
                addCategoryToListAndHashMap(category);
            }
            if ("yes".equals(isFolder)) {
                //이 경우 folder에 해당한다.
                addFolderToHashMap(category, folder);
            }
            if ("no".equals(isCategory) && "no".equals(isFolder)) {
                //일반 item인 case
            }
        }
        adapt.notifyDataSetChanged();
    }


    private String getCurrentTime() {
        // 시스템으로부터 현재시간(ms) 가져오기
        long now = System.currentTimeMillis();
        // Data 객체에 시간을 저장한다.
        Date date = new Date(now);
        // 각자 사용할 포맷을 정하고 문자열로 만든다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);
        return strNow;
    }

}

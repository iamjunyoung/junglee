package com.bbeaggoo.junglee.category;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bbeaggoo.junglee.R;


public class CategoryEditActivity extends AppCompatActivity {

    private final String TAG = "JYN";
    private ItemAdapter adapter;
    private CategoryManager categoryManager;
    private Context mContext;
    private CustomRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        //setContentView(R.layout.category_edit_activity);
        setContentView(R.layout.category_edit_activity_grid);

        recyclerView = (CustomRecyclerView) findViewById(R.id.recyclerview);
        mContext = this;

        adapter = new ItemAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        //
        //recyclerView.setLayoutManager(new ItemLayoutManger(this));
        recyclerView.setLayoutManager(new GridItemLayoutManger(this, 2));

        recyclerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i("JYN", "RecyclerView onClick");
                hideKeyboard(view);
            }
        });

        //http://itpangpang.xyz/44
        /*
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.d("JYN","onInterceptTouchEvent");

                mGestureDetector.onTouchEvent(e);

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    //Log.d("JYN", "e.getX==>" + e.getX());
                    //Log.d("JYN", "e.getY==>" + e.getY());
                    Log.d("JYN", "child==>" + child);

                    Log.d("JYN", "getChildAdapterPosition=>" + rv.getChildAdapterPosition(child));
                    //Log.d("JYN","getChildLayoutPosition=>"+rv.getChildLayoutPosition(child));
                    //Log.d("JYN","getChildViewHolder=>" + rv.getChildViewHolder(child));

                    //Log.d(TAG,"AdapterPosition=>"+rv.findViewHolderForAdapterPosition(rv.getChildLayoutPosition(child)));
                    //Log.d(TAG,"LayoutPosition=>"+rv.findViewHolderForLayoutPosition(rv.getChildLayoutPosition(child)));

                    Log.i(TAG, " : " + adapter.visibleItems.get(rv.getChildAdapterPosition(child)).name
                    + "    type : " + adapter.visibleItems.get(rv.getChildAdapterPosition(child)).viewType);

                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.i("JYN", "onTouchEvent");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                Log.d("JYN","onRequestDisallowInterceptTouchEvent");

            }
        });
        */
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    String userName;
    public void showDialogForInputCategory() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 제목셋팅
        alertDialogBuilder.setTitle("카테고리 추가");
        // AlertDialog 셋팅
        final EditText name = new EditText(this);
        alertDialogBuilder.setView(name);
        alertDialogBuilder
                .setMessage("카테고리 이름")
                .setCancelable(false)
                .setPositiveButton("종료",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 프로그램을 종료한다
                                userName = name.getText().toString();
                                categoryManager = new CategoryManager(mContext);
                                categoryManager.addAndReCreateCategoryMenu(userName);
                                //GridActivity.this.finish();

                                adapter.addParentItem(0, userName);
                                //adapter.reloadData(categoryManager);
                                //아래 4줄을 그냥 추가해버렸다. 추후에 수정하도록 하자
                                /*
                                adapter = new ItemAdapter(mContext);
                                recyclerView.setAdapter(adapter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(mContext));
                                recyclerView.setLayoutManager(new ItemLayoutManger(mContext));
                                */

                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    String folderName;
    public void showDialogForInputFolder(final int holderPosition, final String categoryName) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 제목셋팅
        alertDialogBuilder.setTitle("하위폴더 추가");
        // AlertDialog 셋팅
        final EditText name = new EditText(this);
        alertDialogBuilder.setView(name);
        alertDialogBuilder
                .setMessage("폴더 이름")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                folderName = name.getText().toString();
                                adapter.newAddingFolder(folderName, holderPosition); //adpater에는 holderPosition을 넣는게 맞다
                                categoryManager = new CategoryManager(mContext);
                                categoryManager.addAndReCreateFolderMenu(folderName, categoryName);
                                //GridActivity.this.finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                                folderName = null;
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_for_category_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_category) {
            Toast.makeText(this, "Add category", Toast.LENGTH_SHORT).show();
            showDialogForInputCategory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    final GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            return true;
        }
    });
    */

    private RecyclerView getRecyclerView() {
        return recyclerView;
    }

    GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            View childView = getRecyclerView().findChildViewUnder(e.getX(), e.getY());
            int position = getRecyclerView().getChildPosition(childView);

            Log.i(TAG, "hihi : " + position);
            //ItemClick

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {

            View childView = getRecyclerView().findChildViewUnder(e.getX(), e.getY());
            int position = getRecyclerView().getChildPosition(childView);

            //LongClick

            super.onLongPress(e);
        }
    });

}


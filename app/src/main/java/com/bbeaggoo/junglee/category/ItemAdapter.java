package com.bbeaggoo.junglee.category;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbeaggoo.junglee.R;
import com.bbeaggoo.junglee.category.model.ChildItem;
import com.bbeaggoo.junglee.category.model.Item;
import com.bbeaggoo.junglee.category.model.ParentItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wlsdud.choi on 2016-04-01.
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperListener {

    //    static class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> {

    private final String TAG = "[Simple][NewItemAdaptr]";
    private final int PARENT_ITEM_VIEW = 0;
    private final int CHILD_ITEM_VIEW = 1;

    public ArrayList<Item> items = null;
    public ArrayList<Item> visibleItems = null;

    // JYN for swipe and undo
    //private ArrayList<Item> itemsPendingRemoval;
    private ArrayList<String> itemsPendingRemoval;

    // JYN for long touch select
    public ArrayList<String> longTouchSelectedItems = null;
    Boolean isMultiSelect = false;


    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Integer, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    // JYN End

    private CategoryManager categoryManager;
    private Context mContext;
    private int lastEditedPosition = -1;

    InputMethodManager imm;

    public ItemAdapter(Context context){
        mContext = context;
        categoryManager = new CategoryManager(mContext);

        items = new ArrayList<>();
        visibleItems = new ArrayList<>();

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        Log.i("JYN", "[ItemAdapter ] ItemAdapter is generated");
        /*
        char alphabet = 'A';
        for(int i = 0; i < 10; i++){
            Item item1 = new ParentItem((char)(alphabet+i)+"", PARENT_ITEM_VIEW);
            Item item2 = new ChildItem((char)(alphabet+i)+"-1", CHILD_ITEM_VIEW);
            Item item3 = new ChildItem((char)(alphabet+i)+"-2", CHILD_ITEM_VIEW);
            Item item4 = new ChildItem((char)(alphabet+i)+"-3", CHILD_ITEM_VIEW);

            items.add(item1);
            items.add(item2);
            items.add(item3);
            items.add(item4);

            visibleItems.add(item1);
            visibleItems.add(item2);
            visibleItems.add(item3);
            visibleItems.add(item4);
        }
        */

        // Load list, hashmap and set this to items and visibleItems.
        if (categoryManager.alkitab != null) {
            for (int i = 0 ; i < categoryManager.alkitab.size() ; i++) {
                Item item = new ParentItem(categoryManager.alkitab.get(i), PARENT_ITEM_VIEW);
                items.add(item);
                visibleItems.add(item);
                Log.i("JYN", "[ItemAdapter] add parent : " + categoryManager.alkitab.get(i));

                if ( categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)) != null) {
                    Log.i("JYN", "[ItemAdapter] data_alkitab.get(" + i + ") :" + categoryManager.data_alkitab.get(i));
                    for (int j = 0 ; j < categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).size(); j++) {
                        Item childItem = new ChildItem(categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).get(j), CHILD_ITEM_VIEW);
                        items.add(childItem);
                        visibleItems.add(childItem);
                        Log.i("JYN", "[ItemAdapter] add child : " + categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).get(j) + "    of parent :" + categoryManager.alkitab.get(i));
                    }
                }
            }
        }

        // JYN for swipe and undo
        itemsPendingRemoval = new ArrayList<>();


        // JYN for long touch select
        longTouchSelectedItems = new ArrayList<>();
    }

    public void reloadData(CategoryManager cm) {
        this.categoryManager = cm;
        items = new ArrayList<>();
        visibleItems = new ArrayList<>();

        // Load list, hashmap and set this to items and visibleItems.
        if (categoryManager.alkitab != null) {
            for (int i = 0 ; i < categoryManager.alkitab.size() ; i++) {
                Item item = new ParentItem(categoryManager.alkitab.get(i), PARENT_ITEM_VIEW);
                items.add(item);
                visibleItems.add(item);
                Log.i("JYN", "[ItemAdapter] add parent : " + categoryManager.alkitab.get(i));

                if ( categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)) != null) {
                    Log.i("JYN", "[ItemAdapter] data_alkitab.get(" + i + ") :" + categoryManager.data_alkitab.get(i));
                    for (int j = 0 ; j < categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).size(); j++) {
                        Item childItem = new ChildItem(categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).get(j), CHILD_ITEM_VIEW);
                        items.add(childItem);
                        visibleItems.add(childItem);
                        Log.i("JYN", "[ItemAdapter] add child : " + categoryManager.data_alkitab.get(categoryManager.alkitab.get(i)).get(j) + "    of parent :" + categoryManager.alkitab.get(i));
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount. count : "+ visibleItems.size());

        return visibleItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType. position : "+position+", viewType : "+visibleItems.get(position).viewType+", item : "+ visibleItems.get(position).name);

        return visibleItems.get(position).viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder. viewType : "+viewType);
        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType){
            case PARENT_ITEM_VIEW:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
                viewHolder = new ParentItemVH(view);
                break;
            case CHILD_ITEM_VIEW:
                View subview = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_subitem, parent, false);
                viewHolder = new ChildItemVH(subview);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder. position : "+position);
        if(holder instanceof ParentItemVH){
            Log.i(TAG, "onBindViewHolder. parentItem. "+visibleItems.get(position).name);
            final ParentItemVH parentItemVH = (ParentItemVH)holder;

            if (itemsPendingRemoval.contains(visibleItems.get(position).name)) {
                /** {show swipe layout} and {hide regular layout} */

                parentItemVH.regularLayout.setVisibility(View.GONE);
                parentItemVH.swipeLayout.setVisibility(View.VISIBLE);

                parentItemVH.undo.setTag(holder);
                parentItemVH.delete.setTag(holder);

                final int position2 = position;
                final String removingName = visibleItems.get(position).name;
                Log.i("JYN", "pso : " + position + "    pos2 : " + position2);
                parentItemVH.undo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("JYN", "[ItemAdapter][onBindviewHolder] undo is clicked about " + visibleItems.get(position2).name);
                        undoOpt(position2);
                    }
                });
                parentItemVH.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("JYN", "[ItemAdapter][onBindviewHolder] delete is clicked about " + visibleItems.get(position2).name);
                        remove(removingName);
                    }
                });
            } else {

                parentItemVH.regularLayout.setVisibility(View.VISIBLE);
                parentItemVH.swipeLayout.setVisibility(View.GONE);

                //parentItemVH.regularLayout.setTag(holder);
                //parentItemVH.swipeLayout.setTag(holder);

                parentItemVH.name.setText(visibleItems.get(position).name);

                //기존에 구현되어 있던 부분은 else로 넣어버렸다.
                parentItemVH.name.setTag(holder);
                parentItemVH.add.setTag(holder);
                parentItemVH.edit.setTag(holder);
                parentItemVH.editName.setTag(holder);
                //parentItemVH.arrow.setTag(holder);
                parentItemVH.itemView.setTag(holder);

                parentItemVH.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int holderPosition = ((ParentItemVH) v.getTag()).getAdapterPosition();
                        Log.i("JYN", "add btn clicked : " + holderPosition);
                        String categoryName = visibleItems.get(holderPosition).name;
                        ((CategoryEditActivity) mContext).showDialogForInputFolder(holderPosition, categoryName);
                        //여기선 dialog 띄워주는 것 만 하고
                        //dialog에서 입력되는 값이 있고 - 확인을 누르든, 취소를 누르든
                        //이후 로직은 dialog쪽에서 이쪽에 있는 method를 호출하도록 한다.
                    }
                });

                parentItemVH.editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            Log.d("JYN", "focus loosed");
                            // Do whatever you want here
                        } else {
                            Log.d("JYN", "focused");
                        }
                    }
                });

                parentItemVH.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int holderPosition = ((ParentItemVH) v.getTag()).getAdapterPosition();
                        Log.i("JYN", "Edit btn clicked category : " + visibleItems.get(holderPosition).name +
                                "  position : " + holderPosition);
                        showDialogForEditCategoryName(1, visibleItems.get(holderPosition).name, holderPosition);

                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });

                int subItemSize = getVisibleChildItemSize(position);
                Log.i("JYN", "Category : " + visibleItems.get(position).name +
                        " 's sub folder size : " + subItemSize);
                //if (categoryManager.data_alkitab.get(visibleItems.get(position).name).size() == 0) {
                //여기선 아래와 같이 해줄 필요가 없을 것 같다
                //onBindViewHolder() 시에 보여주는 부분이므로
                //궂이 Runnable객체를 통해 UI를 수정할 필요가 없다.
                //Runnable객체를 전달하여 handler를 통해 UI를 수정하는 코드는
                //새롭게 add 또는 remove 시에 child개수를 채크하여 UI를 바꾸어 주는 부분에 추가하면 될 것이다.
                //그런데 현재 child remove시에 parent가 누군지 등 parent정보를 알 수 없다.
                //알 수 있도록 구현이 추가되어야겠다. (Todo)
                if (subItemSize == 0) {
                    parentItemVH.arrow.setVisibility(View.INVISIBLE);
                    /*
                    Runnable hideExpandIconRunnable = new Runnable() {
                        @Override
                        public void run() {
                            parentItemVH.arrow.setVisibility(View.INVISIBLE);
                        }
                    };
                    handler.post(hideExpandIconRunnable);
                    */
                } else {
                    parentItemVH.arrow.setVisibility(View.VISIBLE);
                    /*
                    Runnable hideExpandIconRunnable = new Runnable() {
                        @Override
                        public void run() {
                            parentItemVH.arrow.setVisibility(View.VISIBLE);
                        }
                    };
                    handler.post(hideExpandIconRunnable);
                    */
                }
                /*
                if (subItemSize >= 1) {
                    parentItemVH.arrow.setVisibility(View.VISIBLE);
                }*/
                parentItemVH.arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int holderPosition = ((ParentItemVH) v.getTag()).getAdapterPosition();
                        Log.i("JYN", "Category : " + visibleItems.get(holderPosition).name + " 's arrow is clicked");

                        if (((ParentItem) visibleItems.get(holderPosition)).visibilityOfChildItems) {
                            collapseChildItems(holderPosition);
                        } else {
                            expandChildItems(holderPosition);
                        }
                    }
                });

                parentItemVH.arrow.setTag(holder);

                parentItemVH.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int holderPosition = ((ParentItemVH) v.getTag()).getAdapterPosition();
                        Log.i("JYN", "Category : " + visibleItems.get(holderPosition).name + " 's itemView is clicked");
                    }
                });

                if (parentItemVH.getItemViewType() == PARENT_ITEM_VIEW) {
                    parentItemVH.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int holderPosition = ((ParentItemVH) v.getTag()).getAdapterPosition();
                            Log.i("JYN", "Category : " + visibleItems.get(holderPosition).name + " 's itemView is long clicked");

                            if (((ParentItem) visibleItems.get(holderPosition)).visibilityOfChildItems) {
                                collapseChildItems(holderPosition);
                            }

                            // JYN for multi select
                            /*
                            if (!isMultiSelect) {
                                Log.i("JYN", "set isMultiSelect to true");
                                isMultiSelect = true;
                                longTouchSelectedItems.add(visibleItems.get(holderPosition).name);
                            } else {
                                Log.i("JYN", "set isMultiSelect to false");
                                isMultiSelect = false;
                                longTouchSelectedItems.remove(visibleItems.get(holderPosition).name);
                            }*/
                            longTouchSelectedItems.add(visibleItems.get(holderPosition).name);
                            notifyItemChanged(holderPosition);

                            return true;
                        }
                    });
                }
            }


            // JYN for long touch select
            if (longTouchSelectedItems.contains(visibleItems.get(position).name)) {
                Log.i("JYN_ViewHolder", "(if) set color to BLUE");
                //parentItemVH.itemView.setBackgroundColor(Color.BLUE);
                parentItemVH.name.setTextColor(Color.BLUE);
            } else {
                //        card_view:cardBackgroundColor="#5cb88b">
                Log.i("JYN_ViewHolder", "(else) set color to Green");
                //parentItemVH.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.gridItem));
                parentItemVH.name.setTextColor(Color.BLACK);
            }

        } else if(holder instanceof ChildItemVH){
            Log.i(TAG, "onBindViewHolder. sub item. "+visibleItems.get(position).name);
            //JYN modify
            ChildItemVH childItemVH = (ChildItemVH)holder;
            childItemVH.edit.setTag(holder);
            childItemVH.itemView.setTag(holder);

            childItemVH.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int holderPosition = ((ChildItemVH)v.getTag()).getAdapterPosition();
                    Log.i("JYN", "Edit btn clicked folder : " + visibleItems.get(holderPosition).name +
                            "  position : " + holderPosition);

                    showDialogForEditCategoryName(0, visibleItems.get(holderPosition).name, holderPosition);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
            });

            childItemVH.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int holderPosition = ((ChildItemVH)v.getTag()).getAdapterPosition();
                    Log.i("JYN", "folder : " + visibleItems.get(holderPosition).name + " 's itemView is clicked") ;
                }
            });

            ((ChildItemVH)holder).name.setText(visibleItems.get(position).name);
        }
    }

    private void undoOpt(int position) {
        Log.i(TAG, "[undoOpt] : "+visibleItems.get(position).name);

        Runnable pendingRemovalRunnable = pendingRunnables.get(position);
        pendingRunnables.remove(position);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(visibleItems.get(position).name);
        // this will rebind the row in "normal" state
        notifyItemChanged(position);
    }

    public void pendingRemoval(final int position) {
        //이때만해도 position에 따른 visibleItem을 제대로 갖고있다.
        Log.i(TAG, "[pendingRemoval] pos : " + position +"    item " +visibleItems.get(position).name);

        if (!itemsPendingRemoval.contains(visibleItems.get(position).name)) {
            itemsPendingRemoval.add(visibleItems.get(position).name);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            final String removingName = visibleItems.get(position).name;
            //여기서 받아온 removingName이 기준이 되어야 한다.
            //이 값이 지워야 할 값 그 자체이며
            //기존처러 position으로 값을 갖고오면, 여기서 얻어오는 값과, run객체에서 실행시 얻어지는 값이 달라지는 경우가 발생한다.
            //그 이유는 visibleItem.remove시에 이미 visibleItem 리스트가 업데이트 되고
            //업데이트된 visibleItem list에 position으로 접근하면 우리가 지우려 했던 값이 아니기 때문이다.
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "inside [pendingRemoval] pos : " + position //+ "    item " +visibleItems.get(position).name
                    + "    removingName : " + removingName);
                    //remove(visibleItems.indexOf(visibleItems.get(position)));
                    remove(removingName);
                    //그냥 position으로 하면 안되는듯...
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(position, pendingRemovalRunnable);
        }
    }

    //public void remove(int position) {
    public void remove(String removingName) {
        Log.i(TAG, "[remove] : " + removingName);
        //Log.i(TAG, "[remove] : " + removingName);

        //if (itemsPendingRemoval.contains(visibleItems.get(position).name)) {
        //    itemsPendingRemoval.remove(visibleItems.get(position).name);
        if (itemsPendingRemoval.contains(removingName)) {
            itemsPendingRemoval.remove(removingName);

            //JYN modify
            //visibleItems.get(position).name
            Log.i("JYN", "onItemSwipe() Delete category : " + removingName);
            categoryManager.deleteCategory(removingName);
            //JYN modify end

            Item foundedItem = null;
            for (int i = 0 ; i < visibleItems.size() ; i++) {
                if (removingName != null && removingName.equals(visibleItems.get(i).name)) {
                    Log.i("JYN", "Found item : " + removingName);
                    foundedItem = visibleItems.get(i);
                }
            }

            //null 체크 추가 해야 할듯
            int newPosition = visibleItems.indexOf(foundedItem);
            Log.i("JYN",  "newPosition : " + newPosition);
            //아래는 다 position이었
            int childItemSize = getVisibleChildItemSize(newPosition);

            Log.i("JYN", "childItemSize : " + childItemSize);
            for(int i = 0; i <= childItemSize; i++){
                Log.i("JYN", "remove childItemSize also : " + visibleItems.get(newPosition).name);
                visibleItems.remove(newPosition);
            }
            notifyItemRangeRemoved(newPosition, childItemSize + 1);

        }

    }

    // not used
    public boolean isPendingRemoval(int position) {
        Log.i(TAG, "[isPendingRemoval] : "+visibleItems.get(position).name);

        return itemsPendingRemoval.contains(visibleItems.get(position).name);
    }

    private void focusCleanEditText() {
        if (lastEditedPosition != -1) {
            // 현재 focus된 editText가 있다면
            // focus되어 있는 editText를 다시 textView로 set해준다.
        }
    }

    private void collapseChildItems(int position){
        Log.i(TAG, "collapseChildItems");
        ParentItem parentItem = (ParentItem)visibleItems.get(position);
        parentItem.visibilityOfChildItems = false;

        int subItemSize = getVisibleChildItemSize(position);
        for(int i = 0; i < subItemSize; i++){
            parentItem.unvisibleChildItems.add((ChildItem) visibleItems.get(position + 1));
            visibleItems.remove(position + 1);
        }
        notifyItemRangeRemoved(position + 1, subItemSize);
    }

    private int getVisibleChildItemSize(int parentPosition){
        int count = 0;
        parentPosition++;
        while(true){
            if(parentPosition == visibleItems.size() || visibleItems.get(parentPosition).viewType == PARENT_ITEM_VIEW){
                break;
            }else{
                parentPosition++;
                count++;
            }
        }
        return count;
    }

    private void expandChildItems(int position){
        Log.i("JYN", "expandChildItems");

        ParentItem parentItem = (ParentItem)visibleItems.get(position);
        parentItem.visibilityOfChildItems = true;
        int childSize = parentItem.unvisibleChildItems.size();

        for(int i = childSize - 1; i >= 0; i--){
            visibleItems.add(position + 1, parentItem.unvisibleChildItems.get(i));
        }
        parentItem.unvisibleChildItems.clear();

        notifyItemRangeInserted(position + 1, childSize);
    }

    public void newAddingFolder(String newAddingFolder, int holderPosition) {
        Log.i("JYN", "newAddingFolder : " + newAddingFolder);
        if(((ParentItem)visibleItems.get(holderPosition)).visibilityOfChildItems){
            if (newAddingFolder != null) {
                addChildItem(holderPosition, newAddingFolder);
            }
        }else{
            if (newAddingFolder != null) {
                expandChildItems(holderPosition);
                addChildItem(holderPosition, newAddingFolder);
            }
        }
        //Expand상태라면 현재와 같이 동작
        //addChildItem(holderPosition);
        //Expand상태가 아니라면 먼저 Expand를 시키고 addChildItem() 호출
    }

    public void addParentItem(int position, String newAddingCategory) {
        Log.i("JYN", "addParentItem to : " + position + "     folder : " + newAddingCategory);

        position = -1;
        items.add(position + 1, new ParentItem(newAddingCategory, PARENT_ITEM_VIEW));
        visibleItems.add(position + 1, new ParentItem(newAddingCategory, PARENT_ITEM_VIEW));
        notifyItemInserted(position + 1);
    }

    private void  addChildItem(int position, String newAddingFolder) {
        Log.i("JYN", "addChildItem to : " + position + "     folder : " + newAddingFolder);

        items.add(position + 1, new ChildItem(newAddingFolder, CHILD_ITEM_VIEW));
        visibleItems.add(position + 1, new ChildItem(newAddingFolder, CHILD_ITEM_VIEW));

        notifyItemInserted(position + 1);
        notifyItemChanged(position);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.i(TAG, "onItemMove. fromPosition : "+fromPosition+", toPosition : "+toPosition);
        Log.i(TAG, "onItemMove. fromItem : "+visibleItems.get(fromPosition).name+", toItem : "+visibleItems.get(toPosition).name);

        if(fromPosition < 0 || fromPosition >= visibleItems.size() || toPosition < 0 || toPosition >= visibleItems.size()){
            return false;
        }

        Item fromItem = visibleItems.get(fromPosition);

        if(visibleItems.get(fromPosition).viewType == CHILD_ITEM_VIEW){
            if(fromPosition <= 0 || toPosition <= 0){
                return false;
            }
            Log.i(TAG, "onItemMove. remove add");

            visibleItems.remove(fromPosition);
            visibleItems.add(toPosition, fromItem);

            notifyItemMoved(fromPosition, toPosition);

        }else{
            if(visibleItems.get(fromPosition).viewType == visibleItems.get(toPosition).viewType) {
                if(fromPosition > toPosition){
                    Log.i(TAG, "onItemMove. remove add");

                    visibleItems.remove(fromPosition);
                    visibleItems.add(toPosition, fromItem);

                    notifyItemMoved(fromPosition, toPosition);
                }else{
                    int toParentPosition = getParentPosition(toPosition);
                    int toLastchildSize = getVisibleChildItemSize(toParentPosition);
                    Log.i(TAG, "onItemMove. lastChild : "+toLastchildSize);
                    if(toLastchildSize == 0){
                        Log.i(TAG, "onItemMove. remove add");

                        visibleItems.remove(fromPosition);
                        visibleItems.add(toPosition, fromItem);

                        notifyItemMoved(fromPosition, toPosition);
                    }
                }
            }else{
                if(fromPosition < toPosition){
                    int toParentPosition = getParentPosition(toPosition);
                    int toLastchildPosition = getVisibleChildItemSize(toParentPosition) + toParentPosition;
                    Log.i(TAG, "onItemMove. lastChild : "+toLastchildPosition);

                    if(toLastchildPosition == toPosition) {
                        Log.i(TAG, "onItemMove. remove add");

                        visibleItems.remove(fromPosition);
                        visibleItems.add(toPosition, fromItem);

                        notifyItemMoved(fromPosition, toPosition);
                    }
                }
            }
        }

        for(int i = 0; i < visibleItems.size(); i++){
            Log.i(TAG, "onItemMove : "+visibleItems.get(i).name);
        }

        return true;
    }

    @Override
    public void onItemRemove(int position) {
        Log.i(TAG, "onItemRemove. pos : " + position + "    item : "+visibleItems.get(position).name);
        switch(visibleItems.get(position).viewType){
            case PARENT_ITEM_VIEW:
                //여기서 분기를 하면 어떨까??
                pendingRemoval(position);

                //원래는 아래처럼 바로 remove를 수행하였으나
                //바로 remove하지 말고, 위 처럼 pendingRemoval을 호출하여 undo로 취소하거나 TINEOUT으로 삭제되도록 하자.
                //JYN modify
                /*
                Log.i("JYN", "onItemSwipe() Delete category : " + visibleItems.get(position).name);
                categoryManager.deleteCategory(visibleItems.get(position).name);
                //JYN modify end

                int childItemSize = getVisibleChildItemSize(position);

                for(int i = 0; i <= childItemSize; i++){
                    visibleItems.remove(position);
                }
                notifyItemRangeRemoved(position, childItemSize + 1);
                */
                break;
            case CHILD_ITEM_VIEW:
                //여기서 분기를 하면 어떨까?


                //JYN modify
                Log.i("JYN", "onItemSwipe() Delete folder : " + visibleItems.get(position).name);
                categoryManager.deleteFolder(visibleItems.get(position).name);
                //JYN modify end


                visibleItems.remove(position);
                notifyItemRemoved(position);

                //아래처럼 해볼까?
                //notifyDataSetChanged();
                //위와같이 notifyDataSetChanged()를 추가하면
                //마지막 child remove시에 parent의 arrow이미지가 INVISIBLE 처리된다.
                //현재는 이 방법 말고는 방법이 없는데
                //Child가 parent의 정보를 알 수 있는 방식으로 구현된다면
                //이 시점에 parent의 child개수를 check하여 runnable 을 통해 UI 를 갱신하는게 가장 좋을 것 같다.
                break;
        }
    }

    @Override
    public void onItemSwipe(int position) {
        Log.i("JYN", "onItemSwipe. item : "+visibleItems.get(position).name);
        if(visibleItems.get(position).viewType == PARENT_ITEM_VIEW){
            Log.i("JYN", "onItemSwipe() Delete category : " + visibleItems.get(position).name);
            categoryManager.deleteCategory(visibleItems.get(position).name);
        }else{
            Log.i("JYN", "onItemSwipe() Delete folder : " + visibleItems.get(position).name);
            categoryManager.deleteFolder(visibleItems.get(position).name);
        }
    }

    private int getParentPosition(int position){
        while(true){
            if(visibleItems.get(position).viewType == PARENT_ITEM_VIEW){
                break;
            }else{
                position--;
            }
        }
        return position;
    }

    public class  ParentItemVH extends RecyclerView.ViewHolder {
        public RelativeLayout regularLayout;
        public RelativeLayout swipeLayout;
        TextView name;
        EditText editName;
        ImageButton edit;
        ImageButton add;
        ImageButton arrow;

        TextView undo;
        TextView delete;

        public ParentItemVH(View itemView) {
            super(itemView);
            Log.i(TAG, "ParentItemVH");
            regularLayout = (RelativeLayout)itemView.findViewById(R.id.regularParentLayout);
            swipeLayout = (RelativeLayout)itemView.findViewById(R.id.swipeParentLayout);
            edit = (ImageButton)itemView.findViewById(R.id.item_edit);
            editName = (EditText)itemView.findViewById(R.id.edit_item_name);
            name = (TextView)itemView.findViewById(R.id.item_name);
            add = (ImageButton)itemView.findViewById(R.id.item_add);;
            arrow = (ImageButton)itemView.findViewById(R.id.item_arrow);

            undo = (TextView)itemView.findViewById(R.id.undo);
            delete = (TextView)itemView.findViewById(R.id.delete);
        }
    }

    public class ChildItemVH extends RecyclerView.ViewHolder {
        public RelativeLayout regularLayout;
        public RelativeLayout swipeLayout;
        ImageButton edit;
        TextView name;
        EditText editName;
        CheckBox checkBox;

        public ChildItemVH(View itemView) {
            super(itemView);
            Log.i(TAG, "ChildItemVH");
            regularLayout = (RelativeLayout)itemView.findViewById(R.id.regularChildLayout);
            swipeLayout = (RelativeLayout)itemView.findViewById(R.id.swipeChildLayout);
            edit = (ImageButton)itemView.findViewById(R.id.subitem_edit);
            name = (TextView)itemView.findViewById(R.id.subitem_name);
            editName = (EditText)itemView.findViewById(R.id.edit_subitem_name);
            checkBox = (CheckBox)itemView.findViewById(R.id.subitem_checkbox);
        }
    }

    String newName = null;
    public void showDialogForEditCategoryName(final int type, final String nameOrigin, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        String title = null;
        if (type == 1) {
            title = "카테고리";
        } else {
            title = "폴더";
        }

        alertDialogBuilder.setTitle(title + "명 변경");
        final EditText name = new EditText(mContext);
        name.setText(nameOrigin);
        name.selectAll();
        alertDialogBuilder.setView(name);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("저장",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                newName = name.getText().toString();
                                if (type == 1) {
                                    categoryManager.updateCategoryColumn(nameOrigin, newName);
                                } else {
                                    categoryManager.updateFolderColumn(nameOrigin, newName);
                                }
                                Log.i("JYN", "notifyDataSetChanged start. item : " + items.get(position).name
                                        + " visibleItem : " + visibleItems.get(position).name);
                                items.get(position).name = newName;
                                items.set(position, items.get(position));

                                visibleItems.get(position).name = newName;
                                visibleItems.set(position, visibleItems.get(position));
                                //notifyDataSetChanged();

                                notifyItemChanged(position);
                                Log.i("JYN", "notifyDataSetChanged end. item : " + items.get(position).name
                                        + " visibleItem : " + visibleItems.get(position).name);
                                imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                                //GridActivity.this.finish();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                                dialog.cancel();

                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

}

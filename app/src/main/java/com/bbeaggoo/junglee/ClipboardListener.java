package com.bbeaggoo.junglee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;

/**
 * Created by junyoung on 16. 7. 23..
 */
public class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {
    @Override
    public void onPrimaryClipChanged() {
        // do something useful here with the clipboard
        // use getText() method
        //saveClipItem(clipBoard.getPrimaryClip().getItemAt(0));
    }

    private void saveClipItem(ClipData.Item item) {
        if( item != null ) {
            String clipboardText = item.getText().toString();
            Log.i("ClipboardListener", "text : " + clipboardText);
        }
    }
}

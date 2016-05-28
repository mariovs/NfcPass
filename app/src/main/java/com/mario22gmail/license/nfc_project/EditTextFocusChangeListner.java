package com.mario22gmail.license.nfc_project;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Mario Vasile on 5/15/2016.
 */
public class EditTextFocusChangeListner implements View.OnFocusChangeListener {

    private int editTextId;
    private Context context;
    public EditTextFocusChangeListner(Context context,int idEditText)
    {
        this.context = context;
        editTextId = idEditText;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v.getId()== editTextId &&  !hasFocus)
        {
            InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}

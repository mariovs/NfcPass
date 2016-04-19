package com.mario22gmail.license.nfc_project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddWebCredentials extends Fragment {


    private String defaultUrl =" ";

    public AddWebCredentials()
    {

    }

    public void SetDefaultUrl(String defaultUrl)
    {
        this.defaultUrl = defaultUrl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_add_web_credentials, container, false);
        EditText urlEditText = (EditText) view.findViewById(R.id.urlTextBox);
        urlEditText.setText(defaultUrl);
        if(!defaultUrl.equals(""))
        {
            urlEditText.setVisibility(View.GONE);
        }
        return view;
    }

}

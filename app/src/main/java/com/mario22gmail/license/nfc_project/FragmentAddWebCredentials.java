package com.mario22gmail.license.nfc_project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddWebCredentials extends Fragment {


    private String defaultUrl ="";
    private String userNameString = "";

    public FragmentAddWebCredentials()
    {

    }

    public void SetDefaultUrl(String defaultUrl)
    {
        this.defaultUrl = defaultUrl;
    }

    public void SetUserName(String userName)
    {
        this.userNameString =userName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_add_web_credentials, container, false);
        final EditText urlEditText = (EditText) view.findViewById(R.id.fragmentAddWebUrlTextBox);
        urlEditText.setText(defaultUrl);
        urlEditText.setSingleLine();
//        EditTextFocusChangeListner  textLisnerWebEditText = new EditTextFocusChangeListner(getContext(),R.id.fragmentAddWebUrlTextBox);
//        urlEditText.setOnFocusChangeListener(textLisnerWebEditText);

        final EditText userNameEditText = (EditText) view.findViewById(R.id.fragmentAddUserNameTextBox);
        userNameEditText.setSingleLine();
//        EditTextFocusChangeListner textLisnerUserEdit = new EditTextFocusChangeListner(getContext(), R.id.fragmentAddUserNameTextBox);
//        userNameEditText.setOnFocusChangeListener(textLisnerUserEdit);

        final EditText passwordEditText = (EditText) view.findViewById(R.id.fragmentAddWebPasswordTextBox);
//        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordEditText.setSingleLine();
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditTextFocusChangeListner textLisnerPasswordEdit = new EditTextFocusChangeListner(getContext(),R.id.fragmentAddWebPasswordTextBox);
        passwordEditText.setOnFocusChangeListener(textLisnerPasswordEdit);


        if(!defaultUrl.equals(""))
        {
            urlEditText.setVisibility(View.GONE);
        }
        if(!userNameString.equals(""))
        {
            userNameEditText.setText(userNameString , TextView.BufferType.EDITABLE);

            Button writeButton = (Button) view.findViewById(R.id.buttonWriteCredentials);
            writeButton.setText("Modifica");
            writeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    WebsitesCredentials credentials = new WebsitesCredentials();
                    credentials.setUserName(userNameEditText.getText().toString());
                    credentials.setUrl(urlEditText.getText().toString());
                    credentials.setPassword(passwordEditText.getText().toString());

                    Intent editIntent = new Intent("editWebCredentialConfirmation");
                    editIntent.putExtra("credential", credentials);
                    NavigationDrawerActivity.getAppContext().sendBroadcast(editIntent);
                }
            });
        }
        return view;
    }

}

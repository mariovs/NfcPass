package com.mario22gmail.license.nfc_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentPinDialog extends DialogFragment{
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.fragment_pin_dialog, null));
//
//
//
////        builder.setMessage("ce faci")
////                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        // FIRE ZE MISSILES!
////                    }
////                })
////                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        // User cancelled the dialog
////                    }
////                });
////        // Create the AlertDialog object and return it
//        return builder.create();
//    }

    private boolean isPinVisible = false;

    public void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pin_dialog,container);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        final EditText pinTextDialog = (EditText) view.findViewById(R.id.editTextPinDialog);
        final NavigationDrawerActivity mainActivity = (NavigationDrawerActivity)getActivity();

        disableSoftInputFromAppearing(pinTextDialog);

        Button buttonEnter = (Button) view.findViewById(R.id.buttonEnterPinDialog);
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumberText = pinTextDialog.getText().toString();
                AuthResponse cardResponse =  mainActivity.AuthenticateOnDesfire(pinNumberText);
                if(cardResponse.isValid())
                {
                    Toast.makeText(getContext(), "Autentificat", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    Log.i("nfc_debug", "Card is valid");
                    Intent goToWebCredentialPage = new Intent("goToWebCredentialPage");
                    NavigationDrawerActivity.getAppContext().sendBroadcast(goToWebCredentialPage);
                }
                else
                {
                    Toast.makeText(getDialog().getContext(), "Pin invalid", Toast.LENGTH_SHORT).show();

                }

            }
        });

        Button cancelDialog = (Button) view.findViewById(R.id.buttonPinDialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Button buttonNumarul1 = (Button) view.findViewById(R.id.buttonNumarul1);
        buttonNumarul1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position, "1");
            }
        });

        Button buttonNumarul2 = (Button) view.findViewById(R.id.buttonNumarul2);
        buttonNumarul2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"2");
            }
        });

        Button buttonNumarul3 = (Button) view.findViewById(R.id.buttonNumarul3);
        buttonNumarul3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"3");
            }
        });

        Button buttonNumarul4 = (Button) view.findViewById(R.id.buttonNumarul4);
        buttonNumarul4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"4");
            }
        });

        Button buttonNumarul5 = (Button) view.findViewById(R.id.buttonNumarul5);
        buttonNumarul5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"5");
            }
        });

        Button buttonNumarul6 = (Button) view.findViewById(R.id.buttonNumarul6);
        buttonNumarul6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"6");
            }
        });

        Button buttonNumarul7 = (Button) view.findViewById(R.id.buttonNumarul7);
        buttonNumarul7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"7");
            }
        });

        Button buttonNumarul8 = (Button) view.findViewById(R.id.buttonNumarul8);
        buttonNumarul8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"8");
            }
        });

        Button buttonNumarul9 = (Button) view.findViewById(R.id.buttonNumarul9);
        buttonNumarul9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"9");
            }
        });

        Button buttonNumarul0 = (Button) view.findViewById(R.id.buttonNumarul0);
        buttonNumarul0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = pinTextDialog.getSelectionStart();
                pinTextDialog.getText().insert(position,"0");
            }
        });

        ImageButton buttonDeleteLastChar = (ImageButton) view.findViewById(R.id.buttonDeleteLastCharPinDialog);
        buttonDeleteLastChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position =pinTextDialog.getSelectionStart();
                if(position > 0)
                {
                    Log.i("nfc_debug", "pozitia e " + position);
                    pinTextDialog.getText().delete(position-1,position);
                }

            }
        });

        final Button pinVisibility = (Button) view.findViewById(R.id.pinVisibility);
        pinTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
        pinVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPinVisible)
                {
                    int position = pinTextDialog.getSelectionStart();
                    pinVisibility.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                    pinTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    pinTextDialog.setTypeface(Typeface.DEFAULT);
                    pinTextDialog.setSelection(position);
                    isPinVisible = false;
                }
                else
                {
                    int position = pinTextDialog.getSelectionStart();
                    pinVisibility.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                    pinTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                    pinTextDialog.setSelection(position);
                    isPinVisible = true;
                }
            }
        });

        return view;
    }
}
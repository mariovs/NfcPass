package com.mario22gmail.license.nfc_project;


import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEnableNFC extends DialogFragment {


    public FragmentEnableNFC() {
        // Required empty public constructor
    }

    private boolean isBackFromSettings = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_enable_nfc, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        Button buttonGoSettings = (Button) view.findViewById(R.id.buttonEnableNfcGoSettingButton);
        buttonGoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        NfcAdapter nfcAdapter;
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        if((nfcAdapter != null && nfcAdapter.isEnabled()) && isBackFromSettings)
        {
            Toast.makeText(this.getActivity(), "NFC pornit", Toast.LENGTH_LONG).show();
            getDialog().dismiss();
        }
        isBackFromSettings = true;
        Log.i("nfc_debug", "dialog is on resume");
        super.onResume();
    }
}

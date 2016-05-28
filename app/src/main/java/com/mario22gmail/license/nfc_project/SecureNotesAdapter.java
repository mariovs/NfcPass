package com.mario22gmail.license.nfc_project;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mario Vasile on 5/23/2016.
 */
public class SecureNotesAdapter extends RecyclerView.Adapter<SecureNotesAdapter.ViewHolder> {
    private ArrayList<SecureNote> secureNotesDataSet;

    public SecureNotesAdapter(ArrayList<SecureNote> secureNotes)
    {
        this.secureNotesDataSet = secureNotes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectSecureNoteTextView;

        public ViewHolder(View v) {
            super(v);
            subjectSecureNoteTextView = (TextView) v.findViewById(R.id.textViewSubjectSecureNote);

        }

        public TextView getSubjectSecureNoteTextView()
        {
            return subjectSecureNoteTextView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.secure_note_element,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.i("nfc_debug","Element secure at " + position);

        RelativeLayout relativeLayout = (RelativeLayout) holder.itemView.findViewById(R.id.RelativeLayoutSecureNote);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecureNote note = secureNotesDataSet.get(position);
                Intent secureNoteIntent = new Intent("start.secure.note");
                secureNoteIntent.putExtra("secureNote",note);
                NavigationDrawerActivity.getAppContext().sendBroadcast(secureNoteIntent);

            }
        });
        holder.getSubjectSecureNoteTextView().setText(secureNotesDataSet.get(position).getSubject());

    }

    @Override
    public int getItemCount() {
        return secureNotesDataSet.size();
    }
}

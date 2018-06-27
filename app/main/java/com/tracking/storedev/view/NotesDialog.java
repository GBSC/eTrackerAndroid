package com.tracking.storedev.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tracking.storedev.R;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.util.Util;

/**
 * Created by ZASS on 3/22/2018.
 */

public class NotesDialog extends Dialog {

    public Util util = Util.getInstance();

    private StoreDetailActivity context;
    private EditText editTextNotes;

    private ProgressBar contactProgressBar;

    private Button addBtn;
    private ImageView closeBtn;

    private ContactCallback contactCallback;
    private TextView textViewSkip;

    public NotesDialog(StoreDetailActivity context, ContactCallback contactCallback) {
        super(context);
        this.context = context;
        this.contactCallback = contactCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notes_dialog);

        editTextNotes = (EditText)findViewById(R.id.editTextNotes);
        contactProgressBar = (ProgressBar) findViewById(R.id.contactProgressBar);
        textViewSkip = (TextView)findViewById(R.id.textViewSkip);
        addBtn =(Button)findViewById(R.id.addContactBtn);
        closeBtn =(ImageView)findViewById(R.id.closeBtn);

        //textTitle.setText(util.getSmallCapsString("Contact Person"));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = editTextNotes.getText().toString();
                if(notes.equals("")){
                    Toast.makeText(context, "Please add notes", Toast.LENGTH_LONG).show();
                    contactProgressBar.setVisibility(View.INVISIBLE);
                }else{
                    contactProgressBar.setVisibility(View.VISIBLE);
                    contactCallback.buttonPress(v,notes );
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactCallback.buttonPress(v,"" );
            }
        });

        textViewSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactCallback.buttonPress(v,"" );
                context.finish();
                dismiss();
            }
        });
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);
    }

    public interface ContactCallback{
        public void buttonPress(View v, String notes);
    }
}


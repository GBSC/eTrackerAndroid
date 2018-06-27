package com.tracking.storedev.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tracking.storedev.R;
import com.tracking.storedev.StoreDetailActivity;
import com.tracking.storedev.db.Names;
import com.tracking.storedev.db.Product;
import com.tracking.storedev.dbcontroller.DBHandler;
import com.tracking.storedev.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZASS on 3/22/2018.
 */

public class ContactPersonDialog extends Dialog {

    public Util util = Util.getInstance();
    public DBHandler dbHandler = DBHandler.getInstance();

    private StoreDetailActivity context;
    private AutoCompleteTextView editTextContactPerson;
    private EditText editTextContactNumber;
    private ProgressBar contactProgressBar;
    private Button addBtn;
    private ImageView closeBtn;
    private ArrayList<Product> productArrayList;
    private ContactCallback contactCallback;


    public long storeID;
    public String name, number;

    public ContactPersonDialog(StoreDetailActivity context,long storeID, ContactCallback contactCallback) {
        super(context);
        this.context = context;
        this.productArrayList = productArrayList;
        this.contactCallback = contactCallback;
        this.storeID = storeID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contactperson_dialog);

        editTextContactPerson = (AutoCompleteTextView) findViewById(R.id.editTextContactPerson);
        editTextContactNumber = (EditText) findViewById(R.id.editTextContactNumber);
        contactProgressBar = (ProgressBar) findViewById(R.id.contactProgressBar);
        addBtn =(Button)findViewById(R.id.addContactBtn);
        closeBtn =(ImageView)findViewById(R.id.closeBtn);

        final List<String> namesList = dbHandler.getContactList(storeID);
        int id = R.layout.sub_section_row;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, id, R.id.textid, namesList);
       // NamesAutocompleteAdapter namesAutocompleteAdapter = new NamesAutocompleteAdapter(context,id, namesList);
        editTextContactPerson.setAdapter(adapter);

        editTextContactPerson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String)parent.getItemAtPosition(position);
                //name = namesList.get(position);
                number = dbHandler.getContactNumber(name);
                editTextContactNumber.setText(""+number);
            }
        });

        //textTitle.setText(util.getSmallCapsString("Contact Person"));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ContactName = editTextContactPerson.getText().toString();
                String ContactNumber = editTextContactNumber.getText().toString();
                if(ContactName.equals("")){
                    Toast.makeText(context, "Please add Contact name", Toast.LENGTH_LONG).show();
                    contactProgressBar.setVisibility(View.INVISIBLE);
                }else if(ContactNumber.equals("")){
                    Toast.makeText(context, "Please add contact number", Toast.LENGTH_LONG).show();
                    contactProgressBar.setVisibility(View.INVISIBLE);
                }else{

                    Names names = new Names();
                    names.name_ = ContactName;
                    names.number_ = ContactNumber;
                    names.storeID = storeID;
                    names.save();

                    contactProgressBar.setVisibility(View.VISIBLE);
                    contactCallback.buttonPress(v, ContactName, ContactNumber);
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
                dismiss();
            }
        });
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);
    }

    public interface ContactCallback{
        public void buttonPress(View v, String contactName, String contactNumber);
    }
}


package com.tracking.storedev.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tracking.storedev.R;
import com.tracking.storedev.adapter.SubSectionAdapter;
import com.tracking.storedev.db.SubSection;
import com.tracking.storedev.util.Constants;
import com.tracking.storedev.util.PrefManager;

import java.util.List;

/**
 * Created by ZASS on 3/22/2018.
 */

public class SubsectionSelectionDialog extends Dialog {

    private Context context;
    PrefManager prefManager = PrefManager.getPrefInstance();
    private String filterStr;
    private SelectionCallBack selectionCallBack;
    private String title;
    private List<SubSection> subsectionList;
    private ListView listView;
    private EditText inputSearch;
    SubSectionAdapter sbSectionAdapter;
    private TextView textViewEmpty;

    public SubsectionSelectionDialog(Context context, String title, List<SubSection> objectList, SelectionCallBack selectionCallBack) {
        super(context);
        this.context = context;
        this.title = title;
        this.selectionCallBack = selectionCallBack;
        this.subsectionList = objectList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subsection_dialog);

        ImageView closeBtn = (ImageView) findViewById(R.id.closeBtn);
        TextView txt_title = (TextView) findViewById(R.id.txt_title);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
        listView = (ListView) findViewById(R.id.listview);

        if (subsectionList.size() > 0) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }
        filterStr = (String) prefManager.get(Constants.Filter, new String());


        sbSectionAdapter = new SubSectionAdapter(context, subsectionList);
        txt_title.setText(title);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubSection selection = subsectionList.get(position);
                selectionCallBack.selection(selection);
                dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                sbSectionAdapter.filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        listView.setAdapter(sbSectionAdapter);
        setCancelable(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public interface SelectionCallBack {
        public void selection(SubSection selection);
    }

}


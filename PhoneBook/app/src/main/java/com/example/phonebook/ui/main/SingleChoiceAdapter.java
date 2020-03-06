package com.example.phonebook.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.phonebook.R;

public class SingleChoiceAdapter extends SimpleCursorAdapter {
    private ListView nowlistView;
    private int SelectedPosition;
    public String SelectedGroup;
    public SingleChoiceAdapter(final Context context, int layout, Cursor cursor, String[] from, int[] to, int flag, ListView listView) {
        super(context, layout, cursor, from, to, flag);
        listView.setAdapter(this);
        nowlistView = listView;
        SelectedGroup = "新联系人";
        SelectedPosition = 0;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.singleGroupName);
                SelectedGroup = text.getText().toString();
                SelectedPosition = position;
                Update();
            }
        });
    }

    private void Update() {
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View result = super.getView(position, convertView, parent);
        if (result != null) {
            RadioButton box = result.findViewById(R.id.singleChoiceBox);
            if (position == SelectedPosition) box.setChecked(true); else box.setChecked(false);
        }
        return result;
    }
}

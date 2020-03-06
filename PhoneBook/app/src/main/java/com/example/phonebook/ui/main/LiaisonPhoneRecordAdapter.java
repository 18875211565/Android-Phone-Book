package com.example.phonebook.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CallLog;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.phonebook.DateOperation;
import com.example.phonebook.R;

public class LiaisonPhoneRecordAdapter extends SimpleCursorAdapter {
    public LiaisonPhoneRecordAdapter(final Context context, int layout, Cursor cursor, String[] from, int[] to, int flag){
        super(context, layout, cursor, from, to, flag);
    }

    @Override
    public void bindView(View view, Context context, Cursor ICursor)
    {
        super.bindView(view, context, ICursor);
        TextView dateView = view.findViewById(R.id.recordDate);
        long time = ICursor.getLong(ICursor.getColumnIndex("date"));
        dateView.setText(DateOperation.LongToString(time));
        TextView recordView = view.findViewById(R.id.recordType);
        int type = ICursor.getInt(ICursor.getColumnIndex("type"));
        if (type == CallLog.Calls.INCOMING_TYPE) { recordView.setText("呼入"); recordView.setTextColor(Color.BLACK); }
        else if (type == CallLog.Calls.OUTGOING_TYPE) { recordView.setText("呼出"); recordView.setTextColor(Color.BLACK); }
        else { recordView.setText("未接"); recordView.setTextColor(Color.RED);}
    }
}
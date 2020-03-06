package com.example.phonebook.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.CallLog;
import android.view.View;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.DateOperation;
import com.example.phonebook.R;

public class RecordListAdapter extends SimpleCursorTreeAdapter{
    private DataBaseOperation operation;
    public RecordListAdapter(Cursor cursor, Context context, int groupLayout, int childLayout, String[] groupfrom, int[] groupTo, String[] childrenFrom, int[] childrenTo) {
        super(context, cursor, groupLayout, groupfrom, groupTo, childLayout, childrenFrom, childrenTo);
        operation = new DataBaseOperation(context, "Record");
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor){
        Cursor childCursor = operation.fetchChildrenPhone(groupCursor.getString(groupCursor.getColumnIndex("phone")));
        childCursor.moveToFirst();
        return childCursor;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean IsExpanded) {
        super.bindGroupView(view, context, cursor, IsExpanded);
        TextView dateView = view.findViewById(R.id.recordGroupDate);
        long time = cursor.getLong(cursor.getColumnIndex("date"));
        dateView.setText(DateOperation.LongToString(time));
        String number = cursor.getString(cursor.getColumnIndex("phone"));
        Cursor query = operation.helper.getReadableDatabase().query("LiaisonMan",null,"phone=?",new String[]{number},null,null,null);
        if (query != null && query.getCount() > 0) {
            query.moveToFirst();
            number = query.getString(2);
        }
        TextView phoneView = view.findViewById(R.id.recordGroupNumber);
        phoneView.setText(number);
        TextView recordView = view.findViewById(R.id.recordGroupType);

        int type = cursor.getInt(cursor.getColumnIndex("type"));
        if (type == CallLog.Calls.INCOMING_TYPE) { recordView.setText("呼入"); recordView.setTextColor(Color.BLACK); phoneView.setTextColor(Color.BLACK); }
        else if (type == CallLog.Calls.OUTGOING_TYPE) { recordView.setText("呼出"); recordView.setTextColor(Color.BLACK); phoneView.setTextColor(Color.BLACK); }
        else { recordView.setText("未接"); recordView.setTextColor(Color.RED); phoneView.setTextColor(Color.RED);}
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor ICursor, boolean IsLastChild)
    {
        super.bindChildView(view, context, ICursor, IsLastChild);
        TextView dateView = view.findViewById(R.id.recordDate);
        long time = ICursor.getLong(ICursor.getColumnIndex("date"));
        dateView.setText(DateOperation.LongToString(time));
        String number = ICursor.getString(ICursor.getColumnIndex("phone"));
        Cursor query = operation.helper.getReadableDatabase().query("LiaisonMan",null,"phone=?",new String[]{number},null,null,null);
        if (query != null && query.getCount() > 0) {
            query.moveToFirst();
            number = query.getString(2);
        }
        TextView phoneView = view.findViewById(R.id.recordNumber);
        phoneView.setText(number);
        TextView recordView = view.findViewById(R.id.recordType);

        int type = ICursor.getInt(ICursor.getColumnIndex("type"));
        if (type == CallLog.Calls.INCOMING_TYPE) { recordView.setText("呼入"); recordView.setTextColor(Color.BLACK); phoneView.setTextColor(Color.BLACK); }
        else if (type == CallLog.Calls.OUTGOING_TYPE) { recordView.setText("呼出"); recordView.setTextColor(Color.BLACK); phoneView.setTextColor(Color.BLACK); }
        else { recordView.setText("未接"); recordView.setTextColor(Color.RED); phoneView.setTextColor(Color.RED);}
    }
}
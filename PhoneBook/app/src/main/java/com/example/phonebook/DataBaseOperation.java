package com.example.phonebook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.Toast;

public class DataBaseOperation {
    public final DataBaseHelper helper;
    private final ContentValues values;
    public DataBaseOperation(Context context, String dbName) {
        helper = new DataBaseHelper(context, dbName, null ,3);
        values = new ContentValues();
        Insert("新联系人");
        Insert("黑名单");
    }

    private final void GetLiaison(LiaisonMan liaisonMan) {
        values.clear();
        values.put("phone",liaisonMan.number);
        values.put("name",liaisonMan.name);
        values.put("groupID",liaisonMan.group);
        values.put("location",liaisonMan.location);
        values.put("file",liaisonMan.file);
    }

    private final void GetPhoneRecord(PhoneRecord record) {
        values.clear();
        values.put("phone",record.number);
        values.put("date", record.date);
        values.put("type",record.type);
        values.put("location",record.location);
    }

    public final void Insert(String group) {
        Cursor cursor = helper.getReadableDatabase().query("LiaisonGroup",new String[]{"name"},"name=?", new String[]{group},null,null,null);
        if (cursor!= null && cursor.getCount() > 0) return;
        values.clear();
        values.put("name",group);
        helper.getWritableDatabase().insert("LiaisonGroup",null,values);
    }

    public final void Insert(LiaisonMan liaisonMan) {
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        GetLiaison(liaisonMan);
        dbWriter.insert("LiaisonMan",null,values);
    }

    public final void Insert(PhoneRecord record) {
        Cursor cursor = helper.getReadableDatabase().query("PhoneRecord",null,"phone=?", new String[]{record.number},null,null,"date DESC");
        if (cursor!=null && cursor.getCount() > 0) {
            SQLiteDatabase dbWriter = helper.getWritableDatabase();
            cursor.moveToFirst();
            if (cursor.getLong(cursor.getColumnIndex("date")) > record.date){
                GetPhoneRecord(record);
                values.put("top",0);
                dbWriter.insert("PhoneRecord",null,values);
            }
            else{
                values.clear();values.put("top",0);
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                dbWriter.update("PhoneRecord",values,"_id=?",new String[]{id+""});
                GetPhoneRecord(record);
                values.put("top",1);
                dbWriter.insert("PhoneRecord",null,values);
            }
            values.clear();values.put("top",0);
        }
        else {
            SQLiteDatabase dbWriter = helper.getWritableDatabase();
            GetPhoneRecord(record);
            values.put("top",1);
            dbWriter.insert("PhoneRecord", null, values);
        }
    }

    public void Update(String number, LiaisonMan liaisonMan){
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        GetLiaison(liaisonMan);
        dbWriter.update("LiaisonMan",values,"phone = ?", new String[]{number});
    }

    public final void UpdateLiaisonGroup(String number, String Group)
    {
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        values.clear();
        values.put("groupID", Group);
        dbWriter.update("LiaisonMan",values,"phone=?",new String[]{number});
    }

    public final void Delete(int type, int id) {
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        switch (type) {
            case 0:
                dbWriter.delete("PhoneRecord","_id=?",new String[]{id+""});
                break;
            case 1:
                dbWriter.delete("LiaisonMan","_id=?",new String[]{id+""});
                break;
                default: break;
        }
    }

    public void DeleteGroup(String group){
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        values.clear();
        values.put("groupID", "新联系人");
        dbWriter.update("LiaisonMan",values,"groupID=?",new String[]{group});
        if (!group.equals("新联系人") && !group.equals("黑名单")) dbWriter.delete("LiaisonGroup","name=?",new String[]{group});
    }

    public void DeleteLiaison(String number) {
        SQLiteDatabase dbWriter = helper.getWritableDatabase();
        dbWriter.delete("LiaisonMan","phone=?",new String[]{number});
    }

    public final Cursor fetchGroup(){
        Cursor result = helper.getReadableDatabase().query("LiaisonGroup",null,null,null,null,null,null);
        return result;
    }

    public final Cursor fetchPhoneGroup() {
        Cursor result = helper.getReadableDatabase().query("PhoneRecord",null,"top=?",new String[]{1+""},null,null,"date DESC");
        return result;
    }

    public final Cursor fetchChildrenPhone(String number)
    {
        Cursor result = helper.getReadableDatabase().query("PhoneRecord",null,"phone=?",new String[]{number},null,null,"date DESC");
        return result;
    }

    public final Cursor fetchChildren(String group, String search){
        Cursor result;
        if (search.equals("")) result= helper.getReadableDatabase().query("LiaisonMan",null,"groupID=?",new String[]{group},null,null,null);
        else result = result= helper.getReadableDatabase().query("LiaisonMan",null,"groupID = ? and name LIKE ?",new String[]{group,"%"+search+"%"},null,null,null);
        return result;
    }

    public void UpdateDataFromSystemRecord(MainActivity activity, Context context) {
        Cursor cursor =  null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[] {CallLog.Calls.NUMBER , CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.GEOCODED_LOCATION};

        SharedPreferences preferences = activity.getSharedPreferences("LastQueryTime", Context.MODE_PRIVATE);
        long lastQueryTime = preferences.getLong("lastQueryTime",0L);

        String[] selectionArgs = new String[] { String.valueOf(lastQueryTime) };
        try {
            cursor = contentResolver.query(uri, projection, "date>?", selectionArgs, "date ASC");
        }
        catch (SecurityException e){
            Toast.makeText(context,"你还没有开启读取系统通话记录权限",Toast.LENGTH_LONG).show();
            cursor = null;
        }
        if (cursor != null) {
            long max = lastQueryTime;
            while (cursor.moveToNext()) {
                Insert(new PhoneRecord(cursor.getString(0), cursor.getLong(1), cursor.getInt(2), cursor.getString(3)));
                long now = cursor.getLong(1);
                if (now > max) max = now;
            }
            SharedPreferences.Editor editor = activity.getSharedPreferences("LastQueryTime", Context.MODE_PRIVATE).edit();
            editor.putLong("lastQueryTime", max);
            editor.commit();
            cursor.close();
        }
    }

    private boolean Exist(String number) {
        Cursor cursor = helper.getReadableDatabase().query("LiaisonMan",new String[]{"phone"},"phone=?", new String[]{number},null,null,null);
        if (cursor!= null && cursor.getCount() > 0) return true; else return false;
    }

    private String getLocation(String phone){
        Cursor cursor = helper.getReadableDatabase().query("PhoneRecord",new String[]{"location"},"phone = ?",new String[]{phone},null,null,null);
        if (cursor!=null && cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("location"));
        }
        return "未知";
    }

    public void UpdateDataFromSystemLiaison(MainActivity activity, Context context) {
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        try {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        }
        catch (SecurityException e) {
            Toast.makeText(context,"你还没有开启读取系统联系人权限",Toast.LENGTH_LONG).show();
            cursor = null;
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0).replace(" ","");
                String name = cursor.getString(1);
                if (!Exist(number)) Insert(new LiaisonMan(number, name, "新联系人", getLocation(number), ""));
            }
            cursor.close();
        }
    }
}

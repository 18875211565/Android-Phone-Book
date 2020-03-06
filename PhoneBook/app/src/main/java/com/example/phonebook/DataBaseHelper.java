package com.example.phonebook;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    public final static String CREATE_RECORD_OPERATION="create table PhoneRecord ("
            +"_id integer primary key autoincrement,"
            +"phone text,"
            +"location text,"
            +"date integer,"
            +"type integer,"
            +"top integer)";

    public final static String CREATE_LIAISON_OPERATION="create table LiaisonMan ("
            +"_id integer primary key autoincrement,"
            +"phone text,"
            +"name text,"
            +"groupID text,"
            +"location text,"
            +"file text)";

    public final static String CREATE_LIAISON_GROUP = "create table LiaisonGroup ("
            +"_id integer primary key autoincrement,"
            +"name text)";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_LIAISON_OPERATION);
        db.execSQL(CREATE_RECORD_OPERATION);
        db.execSQL(CREATE_LIAISON_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
    }
}

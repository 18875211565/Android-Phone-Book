package com.example.phonebook;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.android.internal.telephony.ITelephony;

public class IncomingCallReceiver extends BroadcastReceiver {
    private static HashSet<String> blackList = new HashSet<String>();
    public static void UpdateHashSet(Cursor cursor){
        blackList.clear();
        if (cursor != null)
            while (cursor.moveToNext())
                blackList.add(cursor.getString(cursor.getColumnIndex("phone")));
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ITelephony telephonyService;
        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);
                    if (number != null && blackList.contains(number)) {
                        telephonyService.endCall();
                        Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();
            }
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Toast.makeText(context, "Idle "+ number, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
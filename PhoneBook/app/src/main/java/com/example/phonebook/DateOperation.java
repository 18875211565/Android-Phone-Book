package com.example.phonebook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOperation {
    public static String LongToString(long time)
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }
}

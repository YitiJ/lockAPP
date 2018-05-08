package app.amazing.yiti.jeff.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Date;


public class Logic extends AppCompatActivity {

    private static long[] maxTime =new long[7]; // the max times of the days
    private static String[] placeholder = new String[7];// for the "current max time" section on activity week
    private static long timeOnPhone =50;// code in how to find this value
    private static String password = "";// the password
    private static long dayLimit = 0;// max time on the phone
    private static String logText = "";

    public static String getLogText() {
        return logText;
    }
    public static void setLogText(String logText) {
        Logic.logText = logText;
    }

    public static long getDayLimit(){
        return dayLimit;
    }

    public static String getPlaceholder(int i) {
        return placeholder[i];
    }

    public static void setPlaceholder(int i, String t) {
        Logic.placeholder[i] = t;
    }

    public static long getMaxTime(int i) {
        return maxTime[i];
    }
    public static long[] getMaxTimeArray(){
        return maxTime;
    }
    public static void setMaxTime(int i, long d) {
        Logic.maxTime[i] = d;
    }

    public static long getTimeOnPhone() {
        return timeOnPhone;
    }
    public static void setTimeOnPhone(long time){
        timeOnPhone = time;
    }

    public static String getPassword() {
        return password;
    }
    public static void setPassword(String a) {
        password = a;
    }

    public void savesSettings(){
        SharedPreferences settings = getSharedPreferences("a", Context.MODE_PRIVATE); // DO NOT TRY TO MAKE THESE INSTANTIATIONS CLASS LEVEL
        SharedPreferences.Editor editor = settings.edit();

        for(int i = 0; i <= 6;i++) {
            editor.putLong("max"+i, Logic.getMaxTime(i)); // saves all max times
        }
        for(int i = 0; i <=6;i++) {
            editor.putString("place" + i, Logic.getPlaceholder(i));// saves the text
        }
        editor.putString("log",getLogText());//saves log

        editor.apply();
    }
    public void retrievesSettings(){
        Log.e("TEst","Setting");
        SharedPreferences settings = getSharedPreferences("a", Context.MODE_PRIVATE);
        //settings.edit().clear().commit(); // clears prefs for debugging purposes

        for(int i = 0; i <= 6;i++) {
            Logic.setMaxTime(i, settings.getLong("max"+i, 0));// retrieves previous info
        }
        for(int i = 0; i <= 6;i++) {
            Logic.setPlaceholder(i, settings.getString("place"+i, "0 hours : 0 minutes"));
        }
        Logic.setLogText(settings.getString("log",""));
    }

    public void savesPassword(){
        SharedPreferences settings = getSharedPreferences("b", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("pass", password).apply();
    }
    public void retrievesPassword(){
        SharedPreferences settings = getSharedPreferences("b", Context.MODE_PRIVATE);
     // settings.edit().clear().commit();
       Logic.setPassword(settings.getString("pass", ""));
    }

    protected static void setDayLimit() {//sets current max time based on current day
        String TAG = "day";
        switch (new java.util.GregorianCalendar().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                android.util.Log.d(TAG, "monday");
                dayLimit = Logic.getMaxTime(0);
                break;
            case Calendar.TUESDAY:
                android.util.Log.d(TAG, "tuesday");
                dayLimit = Logic.getMaxTime(1);
                break;
            case Calendar.WEDNESDAY:
                android.util.Log.d(TAG, "wednesday");
                dayLimit = Logic.getMaxTime(2);
                break;
            case Calendar.THURSDAY:
                android.util.Log.d(TAG, "thursday");
                dayLimit = Logic.getMaxTime(3);
                break;
            case Calendar.FRIDAY:
                android.util.Log.d(TAG, "friday");
                dayLimit = Logic.getMaxTime(4);
                break;
            case Calendar.SATURDAY:
                android.util.Log.d(TAG, "saturday");
                dayLimit = Logic.getMaxTime(5);
                break;
            case Calendar.SUNDAY:
                android.util.Log.d(TAG, "sunday");
                dayLimit = Logic.getMaxTime(6);
                break;
        }
    }

}

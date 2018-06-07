package app.amazing.yiti.jeff.myapplication;
        import android.app.Service;
        import android.content.*;
        import android.os.*;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
        import android.widget.Toast;
        import android.os.Handler;

        import java.text.DateFormat;
        import java.util.Calendar;
        import java.util.Date;

public class BackgroundService extends Service {
    private boolean isDestroy;
    private long startTime;
    private long storedTime;
    private long totalTime;
    private boolean isStop = true;
    private boolean isWarned = false;
    private long timeLimit;
    private int curDay;
    private SharedPreferences thisSharedPref;
    public static final String DOMAIN = "com.amazing.yiti.jeff.myApplication.BackgroundService";
    public static final String STORED_TIME_KEY = "STORED_TIME";
    public static final String STOP_KEY = "STOP_SIGNAL";
    public static final String CUR_DAY_KEY = "CUR_DAY";
    public static final String WARNED_KEY = "IS_WARNED";
    private MyBinder binder = new MyBinder();
    private BroadcastReceiver limitReciever = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            int mode = intent.getIntExtra("mode",-1);
            if(mode == curDay) setTimeLimit(intent.getIntExtra("limit",0));
        }
    };
    public void sendTime(long totalTime){
        Log.e("SendTime", Long.toString(totalTime));
        Log.e("limit", Long.toString(timeLimit));
        Intent in = new Intent();
        in.putExtra("TIME", totalTime);
        in.setAction("TIMER");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }
    public long getLapseTime(){
        return  (System.currentTimeMillis() - startTime)/1000;
    }
    public void resetStartTimer(){
        startTime = System.currentTimeMillis();
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Binded");
        LocalBroadcastManager.getInstance(BackgroundService.this).registerReceiver(limitReciever, new IntentFilter("LIMIT"));
        return binder;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.v("Bind", "Rebind");
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("Bind", "Unbind");
        super.onUnbind(intent);
        return true;
    }
    public int getLimit(int day){
        SharedPreferences settingSharedPreference = getSharedPreferences("setting",Context.MODE_PRIVATE);
        return settingSharedPreference.getInt("limit"+day,0);
    }
    @Override
    public void onCreate(){
        Log.e("thread","create()");
        thisSharedPref = getSharedPreferences(BackgroundService.DOMAIN, Context.MODE_PRIVATE);
        isStop = thisSharedPref.getBoolean(STOP_KEY,true);
        isWarned = thisSharedPref.getBoolean(WARNED_KEY,false);
        storedTime = thisSharedPref.getLong(STORED_TIME_KEY,-1);
        curDay = thisSharedPref.getInt(CUR_DAY_KEY,0);
        setTimeLimit(getLimit(curDay));
        Log.e("CurrentDay", Long.toString(curDay));
        isDestroy = true;
        runCheckDay();
        runTimer();
    }
    public void runCheckDay(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
               while(isDestroy) {
                   updateCurDay();
               }
            }
        });
        thread.start();
    }
    public void runTimer(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                resetStartTimer();
                totalTime = 0;
                setTimeLimit(getLimit(curDay));
                while(!isStop){
                    try {
                        if(checkDisplay()){
                            totalTime = storedTime + getLapseTime();
                            sendTime(totalTime);
                        }
                        else{
                            resetStartTimer();
                            storedTime = totalTime;
                        }
                        thisSharedPref.edit().putLong(STORED_TIME_KEY,totalTime).apply();
                        checkTimeLimit(totalTime);
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
                storedTime = totalTime;
            }
        });
        thread.start();
    }
    public void updateCurDay(){
        int newDay = 0;
        switch (new java.util.GregorianCalendar().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                newDay = 0;
                break;
            case Calendar.TUESDAY:
               newDay = 1;
                break;
            case Calendar.WEDNESDAY:
                newDay = 2;
                break;
            case Calendar.THURSDAY:
                newDay = 3;
                break;
            case Calendar.FRIDAY:
                newDay = 4;
                break;
            case Calendar.SATURDAY:
                newDay = 5;
                break;
            case Calendar.SUNDAY:
                newDay = 6;
                break;
        }
        if(newDay != curDay){
            Log.e("curDay",Integer.toString(curDay));
            Log.e("newDay",Integer.toString(newDay));
            SharedPreferences settingSharedPreference = getSharedPreferences("setting",Context.MODE_PRIVATE);
            String newText = settingSharedPreference.getString("log","");
            switch (curDay){
                case 0:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Monday ";
                    break;
                case 1:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Tuesday ";
                    break;
                case 2:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Wednesday ";
                    break;
                case 3:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Thursday ";
                    break;
                case 4:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Friday ";
                    break;
                case 5:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Saturday ";
                    break;
                case 6:
                    newText += DateFormat.getDateTimeInstance().format(new Date())+"- Sunday ";
                    break;
            }
            Log.e("CurTimeChange", totalTime+"");
            newText += "Remain Time: " + (timeLimit -totalTime/60 - 1) + "minutes\n";
            settingSharedPreference.edit().putString("log",newText).apply();
            resetTimer();
            curDay = newDay;
            setTimeLimit(getLimit(curDay));
            thisSharedPref.edit().putInt(CUR_DAY_KEY,curDay).apply();
            isWarned = false;
            thisSharedPref.edit().putBoolean(WARNED_KEY,isWarned).apply();
        }
    }
    public long getCurLimit(){
        return timeLimit;
    }
    public long getTimeOnPhone(){
        return (thisSharedPref.getLong(STORED_TIME_KEY, 0))/60;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        Log.e("service","Destroy!!");
        setStop(true);
       isDestroy = false;
    }
    public boolean checkDisplay(){
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT_WATCH&&pm.isInteractive()
                ||Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT_WATCH&&pm.isScreenOn();
    }
    public void resetTimer(){
        thisSharedPref.edit().putLong(STORED_TIME_KEY,0).apply();
        storedTime = 0;
        resetStartTimer();
        Log.e("Reset","reseted");
        sendTime(storedTime);
    }
    public void setStop(boolean isStop){
        if(this.isStop && !isStop){
            this.isStop = false;
            runTimer();
        }
        this.isStop = isStop;
        thisSharedPref.edit().putBoolean(STOP_KEY,isStop).apply();
    }
    public void setTimeLimit(long limit){
        Log.e("setTimeLimit",""+limit);
        if(limit>timeLimit){
            timeLimit = limit;
            setStop(false);
        }
        timeLimit = limit;
    }
    public void checkTimeLimit(long current){
        if(timeLimit > 0 && current/60 >= timeLimit){
            timeLimitReached();
        }
    }
    public void timeLimitReached(){
        if(!isWarned) {
            isWarned = true;
            thisSharedPref.edit().putBoolean(WARNED_KEY,isWarned).apply();
            Handler h = new Handler(getApplicationContext().getMainLooper());
            // Although you need to pass an appropriate context
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Your time limit is reached!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    class MyBinder extends Binder{
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }
}

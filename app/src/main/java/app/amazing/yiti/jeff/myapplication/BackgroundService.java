package app.amazing.yiti.jeff.myapplication;
        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Binder;
        import android.os.Build;
        import android.os.IBinder;
        import android.os.PowerManager;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
        import android.widget.Toast;
        import android.os.Handler;
        import java.util.Calendar;

public class BackgroundService extends Service {
    private long startTime;
    private long storedTime;
    private boolean isStop = false;
    private long timeLimit;
    private int curDay;
    private long maxTime[] = new long[7];
    private SharedPreferences thisSharedPref;
    public static final String DOMAIN = "com.amazing.yiti.jeff.myApplication.BackgroundService";
    public static final String STORED_TIME_KEY = "STORED_TIME";
    public static final String TIME_LIMIT_KEY = "TIME_LIMIT";
    public static final String STOP_KEY = "STOP_SIGNAL";
    public static final String CUR_DAY_KEY = "CUR_DAY";
    private MyBinder binder = new MyBinder();

    public void setMaxTime(){
        maxTime = Logic.getMaxTimeArray();
    }

    public void sendTime(long totalTime){
        Log.e("SendTime", Long.toString(totalTime));
        Log.e("limit", Long.toString(maxTime[curDay]));
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
        timeLimit = maxTime[curDay];
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
    @Override
    public void onCreate(){
        Log.e("thread","create()");
        thisSharedPref = BackgroundService.this.getSharedPreferences(DOMAIN, Context.MODE_PRIVATE);
        isStop = thisSharedPref.getBoolean(STOP_KEY,false);
        storedTime = thisSharedPref.getLong(STORED_TIME_KEY,-1);
        curDay = thisSharedPref.getInt(CUR_DAY_KEY,0);
        timeLimit = maxTime[curDay];
        Log.e("CurrentDay", Long.toString(curDay));
        runTimer();
    }
    public void runTimer(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                resetStartTimer();
                long totalTime = 0;
                while(!isStop){
                    updateCurDay();
                    setTimeLimit(maxTime[curDay]);
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
            resetTimer();
            curDay = newDay;
            thisSharedPref.edit().putInt(CUR_DAY_KEY,curDay).apply();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        Log.e("service","Destroy!!");
        setStop(true);
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
        if(limit>timeLimit){
            timeLimit = limit;
            setStop(false);
        }
        timeLimit = limit;
        thisSharedPref.edit().putLong(TIME_LIMIT_KEY,timeLimit).apply();
    }
    public void checkTimeLimit(long current){
        if(timeLimit > 0 && current/60 >= timeLimit){
            timeLimitReached();
        }
    }
    public void timeLimitReached(){
        setStop(true);
        Handler h = new Handler(getApplicationContext().getMainLooper());
        // Although you need to pass an appropriate context
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Your time limit is reached!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    class MyBinder extends Binder{
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }
}

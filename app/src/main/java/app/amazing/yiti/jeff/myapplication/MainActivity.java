package app.amazing.yiti.jeff.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private boolean isBinded = false;
    private Intent serviceIntent;
    private BackgroundService boundS;
    private SharedPreferences settingSharedPreference;

    private ServiceConnection sConnection = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name){
            Log.e("Bind", "Disconnect");
            isBinded = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("Bind", "connect");
            BackgroundService.MyBinder myBinder = (BackgroundService.MyBinder) service;
            boundS = myBinder.getService();
            isBinded = true;
            setProgressBar(boundS.getTimeOnPhone(),boundS.getCurLimit());
        }
    };
    private BroadcastReceiver curTimeReciever = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            long time = intent.getLongExtra("TIME",0);
            if(boundS!=null) {
                setProgressBar(time,boundS.getCurLimit());
            }
        }
    };
    //Methods
    private void setProgressBar(Long timeOnPhone,long limit){
        TextView percent = findViewById(R.id.percent);
        progressBar = findViewById(R.id.progressBar);
        TextView h;
        TextView m;
        TextView usedTime;
        h = findViewById(R.id.hrView);
        m = findViewById(R.id.mnView);
        {
            usedTime = findViewById(R.id.usedTimeView);
            long time = timeOnPhone;
            int hrs =(int) time/3600;
            time = time%3600;
            int min = (int) time/60;
            time = time%60;
            usedTime.setText(hrs + " hrs " + min + " mins "
                    + time + "s used");
        }
        timeOnPhone /= 60;
        if (limit != 0) { // checks if timer exists
            Log.e("Progressbar","Day Limit:"+limit);
            Log.e("ProgressBar", "Timeonphone"+timeOnPhone);
            double newPerc = 100 - (timeOnPhone / (double)limit * 100);
            percent.setText(String.format("%.2f",newPerc)+"%");//sets % test
            progressBar.setProgress((int) (100 - (timeOnPhone /(double) limit * 100)));// sets bar
            int hour = (int) (limit - timeOnPhone) / 60;
            int minute = (int) (limit - timeOnPhone) % 60;
            h.setText(hour + "");
            m.setText(minute + "");
        }
        else {
            h.setText(0 + "");
            m.setText(0 + "");
            percent.setText("No limit has been set");
        }
    }

    public void initService(){
        Log.e("Test","InitServ");
        serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
        bindService(serviceIntent,sConnection, Context.BIND_AUTO_CREATE);
    }

    public String getPassword(){
        return settingSharedPreference.getString("pass","");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(curTimeReciever, new IntentFilter("TIMER"));
        settingSharedPreference = getSharedPreferences("setting",Context.MODE_PRIVATE);
        initService();
        setContentView(R.layout.activity_main);
        {
            SharedPreferences serviceSharedPreference = getSharedPreferences(BackgroundService.DOMAIN, Context.MODE_PRIVATE);
            Button btn = findViewById(R.id.stopBtn);
            if (serviceSharedPreference.getBoolean(BackgroundService.STOP_KEY, false))
                btn.setText("Start Timer");
            else
                btn.setText("Stop Timer");
        }
        if(getPassword().equals("")){//if password isnt present, prompt user to set one
            startActivity(new Intent(getApplicationContext(), ChangePassword.class));
        }
        findViewById(R.id.toLog).setOnClickListener(new View.OnClickListener() {// log button
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),log.class);
                in.putExtra("log",settingSharedPreference.getString("log",""));
                startActivity(in);
            }
        });
        findViewById(R.id.toWeek).setOnClickListener(new View.OnClickListener() {// Your Week button
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Week.class));
            }
        });
        findViewById(R.id.passwordChange).setOnClickListener(new View.OnClickListener() {// password change button
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Enter Password (case sensitive)");
                final EditText et = new EditText(MainActivity.this);

                 ad.setView(et);
                ad.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(et.getText().toString().equals(getPassword())){
                            startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                        }
                        else{
                            Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ad.create().show();
            }
        });
    }

    public void onResume(){
        super.onResume();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isBinded) {
            unbindService(sConnection);
            isBinded = false;
        }
    }

    public void onStopStartPressed(View v) {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("Enter Password (case sensitive)");
        final EditText et = new EditText(MainActivity.this);

        ad.setView(et);
        ad.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (et.getText().toString().equals(getPassword())) {
                    SharedPreferences serviceSharedPreference = getSharedPreferences(BackgroundService.DOMAIN, Context.MODE_PRIVATE);
                    Button btn = findViewById(R.id.stopBtn);
                    if (serviceSharedPreference.getBoolean(BackgroundService.STOP_KEY, false)) {
                        boundS.setStop(false);
                        btn.setText("Stop Timer");
                    } else {
                        boundS.setStop(true);
                        btn.setText("Start Timer");
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.create().show();

    }
}

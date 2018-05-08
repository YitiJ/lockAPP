package app.amazing.yiti.jeff.myapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Logic {
    private ProgressBar progressBar;
    private boolean isBinded = false;
    private Intent serviceIntent;
    private BackgroundService boundS;
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
            boundS.setMaxTime();
            Logic.setDayLimit();
            setProgressBar();
        }
    };
    private BroadcastReceiver curTimeReciever = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            long time = intent.getLongExtra("TIME",0);
            Logic.setTimeOnPhone(time/60);
            Logic.setDayLimit();
            setProgressBar();
        }
    };

    //Methods
    private void setProgressBar(){
        TextView percent = findViewById(R.id.percent);
        progressBar = findViewById(R.id.progressBar);
        TextView h;
        TextView m;

        if (getDayLimit() != 0) { // checks if timer exists
            if(getTimeOnPhone()<0) return; // stops updating if the limit is reached
            double newPerc = 100 - (Logic.getTimeOnPhone() / (double)getDayLimit() * 100);
            percent.setText(String.format("%.2f",newPerc)+"%");//sets % test
            progressBar.setProgress((int) (100 - (Logic.getTimeOnPhone() /(double) getDayLimit() * 100)));// sets bar
            int hour = (int) (getDayLimit() - Logic.getTimeOnPhone()) / 60;
            int minute = (int) (getDayLimit() - Logic.getTimeOnPhone()) % 60;
            h = findViewById(R.id.hrView);
            h.setText(hour + "");
            m = findViewById(R.id.mnView);
            m.setText(minute + "");
        } else {
            h = findViewById(R.id.hrView);
            h.setText(0 + "");
            m = findViewById(R.id.mnView);
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(curTimeReciever, new IntentFilter("TIMER"));
        retrievesSettings();
        initService();
        retrievesPassword();
        setContentView(R.layout.activity_main);
        if(Logic.getPassword().equals("")){//if password isnt present, prompt user to set one
            startActivity(new Intent(getApplicationContext(), ChangePassword.class));
        }
        findViewById(R.id.toLog).setOnClickListener(new View.OnClickListener() {// log button
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), log.class));
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
        retrievesSettings();
        setProgressBar();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isBinded) {
            unbindService(sConnection);
            boundS.setMaxTime();
            isBinded = false;
        }
    }

}

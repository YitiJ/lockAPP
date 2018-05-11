package app.amazing.yiti.jeff.myapplication;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

public class Week extends AppCompatActivity implements View.OnClickListener{
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_week);

            Button mondayBtn = findViewById(R.id.mondayBtn);
            Button tuesdayBtn = findViewById(R.id.tuesdayBtn);
            Button wednesdayBtn = findViewById(R.id.wednesdayBtn);
            Button thursdayBtn = findViewById(R.id.thursdayBtn);
            Button fridayBtn = findViewById(R.id.fridayBtn);
            Button saturdayBtn = findViewById(R.id.saturdayBtn);
            Button sundayBtn = findViewById(R.id.sundayBtn);

            mondayBtn.setOnClickListener(this);
            tuesdayBtn.setOnClickListener(this);
            thursdayBtn.setOnClickListener(this);
            wednesdayBtn.setOnClickListener(this);
            fridayBtn.setOnClickListener(this);
            sundayBtn.setOnClickListener(this);
            saturdayBtn.setOnClickListener(this);

        }

        protected void onResume(){// sets value of current limits
            super.onResume();
            SharedPreferences settingSharedPreference = getSharedPreferences("setting", Context.MODE_PRIVATE);
            TextView mon =  findViewById(R.id.monCur);
            mon.setText(settingSharedPreference.getString("place0","0 hours: 0 minutes"));
            TextView tue =  findViewById(R.id.tueCur);
            tue.setText(settingSharedPreference.getString("place1","0 hours: 0 minutes"));
            TextView wed =  findViewById(R.id.wedCur);
            wed.setText(settingSharedPreference.getString("place2","0 hours: 0 minutes"));
            TextView thur =  findViewById(R.id.thurCur);
            thur.setText(settingSharedPreference.getString("place3","0 hours: 0 minutes"));
            TextView fri =  findViewById(R.id.friCur);
            fri.setText(settingSharedPreference.getString("place4","0 hours: 0 minutes"));
            TextView sat =  findViewById(R.id.satCur);
            sat.setText(settingSharedPreference.getString("place5","0 hours: 0 minutes"));
            TextView sun =  findViewById(R.id.sunCur);
            sun.setText(settingSharedPreference.getString("place6","0 hours: 0 minutes"));
        }

    public void onClick(View v) {// go to time input xml with right key passed
        final View a = v;
        AlertDialog.Builder ad = new AlertDialog.Builder(Week.this);
        ad.setTitle("Enter Password (case sensitive)");
        final EditText et = new EditText(Week.this);
        ad.setView(et);
        ad.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences settingSharedPreference = getSharedPreferences("setting",Context.MODE_PRIVATE);
                if(et.getText().toString().equals(settingSharedPreference.getString("pass",""))){

                    Intent sender = new Intent(Week.this,Time_Input.class);
                    switch (a.getId()) {
                        case R.id.mondayBtn:
                            sender.putExtra("day",0);//key to be passed
                            break;
                        case R.id.tuesdayBtn:
                            sender.putExtra("day",1);
                            break;
                        case R.id.wednesdayBtn:
                            sender.putExtra("day",2);
                            break;
                        case R.id.thursdayBtn:
                            sender.putExtra("day",3);
                            break;
                        case R.id.fridayBtn:
                            sender.putExtra("day",4);
                            break;
                        case R.id.saturdayBtn:
                            sender.putExtra("day",5);
                            break;
                        case R.id.sundayBtn:
                            sender.putExtra("day",6);
                            break;
                    }
                    startActivity(sender);

                }
                else{
                    Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.create().show();
    }
}

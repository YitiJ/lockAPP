package app.amazing.yiti.jeff.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Week extends Logic implements View.OnClickListener{



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

            TextView mon =  findViewById(R.id.monCur);
            mon.setText(Logic.getPlaceholder(0));
            TextView tue =  findViewById(R.id.tueCur);
            tue.setText(Logic.getPlaceholder(1));
            TextView wed =  findViewById(R.id.wedCur);
            wed.setText(Logic.getPlaceholder(2));
            TextView thur =  findViewById(R.id.thurCur);
            thur.setText(Logic.getPlaceholder(3));
            TextView fri =  findViewById(R.id.friCur);
            fri.setText(Logic.getPlaceholder(4));
            TextView sat =  findViewById(R.id.satCur);
            sat.setText(Logic.getPlaceholder(5));
            TextView sun =  findViewById(R.id.sunCur);
            sun.setText(Logic.getPlaceholder(6));
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
                if(et.getText().toString().equals(getPassword())){

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

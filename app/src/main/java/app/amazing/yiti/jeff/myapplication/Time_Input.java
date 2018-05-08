package app.amazing.yiti.jeff.myapplication;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class Time_Input extends Logic {

    private EditText hour;
    private EditText minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time__input);

        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.min);


       findViewById(R.id.setId).setEnabled(false);// cnt press button if no input
        hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!hour.getText().toString().equals("")&&!minute.getText().toString().equals("")) findViewById(R.id.setId).setEnabled(true);
                else findViewById(R.id.setId).setEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        minute.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!hour.getText().toString().equals("")&&!minute.getText().toString().equals("")) findViewById(R.id.setId).setEnabled(true);
                else findViewById(R.id.setId).setEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        findViewById(R.id.setId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder er = new AlertDialog.Builder(Time_Input.this);
                er.setMessage("Please enter numbers that are within range.").setCancelable(true);

                hour = findViewById(R.id.hour);
                int h = Integer.parseInt(hour.getText().toString());
                minute = findViewById(R.id.min);
                int m = Integer.parseInt(minute.getText().toString());

                int sum = Integer.parseInt(hour.getText().toString()) * 60//hours
                        + Integer.parseInt(minute.getText().toString());//minutes

                if((h>23 || h<0) && (m>60 || m<0))
                    Toast.makeText(getBaseContext(),"Incompatible input : Hours should be between 0 and 23 and Minutes should be between 0 and 60 ",Toast.LENGTH_LONG).show();

                else if(h>23 || h<0)
                    Toast.makeText(getBaseContext(),"Incompatible input : Hours should be between 0 and 23",Toast.LENGTH_SHORT).show();

                else if(m>60 || m<0)
                    Toast.makeText(getBaseContext(), "Incompatible input : Minutes should be between 0 and 60", Toast.LENGTH_SHORT).show();

                else if(sum<Logic.getTimeOnPhone())
                    Toast.makeText(getBaseContext(), "Incompatible input : Time limit should be greater than time already spent on the phone", Toast.LENGTH_SHORT).show();

                else {
                    int mode = getIntent().getIntExtra("day",0);

                    String TAG ="asd";
                    Log.d(TAG, mode+"");
                    Logic.setMaxTime(mode,sum);// in minutes
                    setDayLimit();
                    Logic.setPlaceholder(mode,h+" hours : "+m+" minutes");// for setting the value of current limits
                    Toast.makeText(getBaseContext(), "Set New Limit!", Toast.LENGTH_SHORT).show();
                    appendLog(mode);//updates log
                    savesSettings();//saves data
                    Time_Input.super.onBackPressed();

                }

            }
        });

    }
    public static void appendLog(int i){// im sorry yiti this is so ugly
        String text = "";
        switch (i){
            case 0:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Monday Limit Changed \n";
                break;
            case 1:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Tuesday Limit Changed \n";
                break;
            case 3:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Wednesday Limit Changed \n";
                break;
            case 4:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Thursday Limit Changed \n";
                break;
            case 5:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Friday Limit Changed \n";
                break;
            case 6:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Saturday Limit Changed \n";
                break;
            case 7:
                text = DateFormat.getDateTimeInstance().format(new Date())+" - Sunday Limit Changed \n";
                break;
        }
        setLogText( getLogText() + text); // appended change to log
    }

}

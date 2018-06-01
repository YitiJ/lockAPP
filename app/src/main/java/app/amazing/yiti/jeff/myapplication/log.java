package app.amazing.yiti.jeff.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class log extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TextView logTV = findViewById(R.id.logTextView);
        logTV.setMovementMethod(new ScrollingMovementMethod());
        Log.e("log",getIntent().getStringExtra("log"));
        logTV.setText(getIntent().getStringExtra("log"));
    }
    public void clearLog(View v){
        final SharedPreferences settingSharedPreference = getSharedPreferences("setting", Context.MODE_PRIVATE);
        //Ask for password
        AlertDialog.Builder ad = new AlertDialog.Builder(log.this);
        ad.setTitle("Enter Password (case sensitive)");
        final EditText et = new EditText(log.this);

        ad.setView(et);
        ad.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(et.getText().toString().equals(settingSharedPreference.getString("pass", ""))){
                    //clear data
                    settingSharedPreference.edit().putString("log", "").apply();
                    TextView logTV = findViewById(R.id.logTextView);
                    logTV.setText("");
                }
                else{
                    Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ad.create().show();
    }
}

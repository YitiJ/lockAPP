package app.amazing.yiti.jeff.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.Date;

public class ChangePassword extends AppCompatActivity {

    private TextView newPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPass = findViewById(R.id.newPass);

        findViewById(R.id.setNewPass).setEnabled(false);// cant press button if no input
        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!newPass.getText().toString().equals("")) findViewById(R.id.setNewPass).setEnabled(true);
                else findViewById(R.id.setNewPass).setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        findViewById(R.id.setNewPass).setOnClickListener(new View.OnClickListener() {// button listener
            @Override
            public void onClick(View v) {
                SharedPreferences settingSharedPreference = getSharedPreferences("setting", Context.MODE_PRIVATE);
                newPass = findViewById(R.id.newPass); // sets password when button is pressed
                settingSharedPreference.edit().putString("pass",newPass.getText().toString()).apply();
                String text = DateFormat.getDateTimeInstance().format(new Date())+" - Password Changed \n";

                settingSharedPreference.edit().putString("log",settingSharedPreference.getString("log","")+text).apply();
                Toast.makeText(getBaseContext(),"Password set!",Toast.LENGTH_SHORT).show();
                ChangePassword.super.onBackPressed();
            }
        });
    }
}

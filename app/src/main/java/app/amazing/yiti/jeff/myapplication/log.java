package app.amazing.yiti.jeff.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class log extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        TextView logTV = findViewById(R.id.logTextView);
        logTV.setMovementMethod(new ScrollingMovementMethod());
        logTV.setText(getIntent().getStringExtra("log"));

    }
}

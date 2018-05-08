package app.amazing.yiti.jeff.myapplication;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class log extends Logic {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        retrievesSettings();
        TextView logTV = findViewById(R.id.logTextView);
        logTV.setMovementMethod(new ScrollingMovementMethod());
        logTV.setText(Logic.getLogText());

    }
}

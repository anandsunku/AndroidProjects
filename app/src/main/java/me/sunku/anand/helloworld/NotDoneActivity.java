package me.sunku.anand.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.sunku.anand.helloworld.R;

/**
 * Created by anand on 14/08/16.
 */

public class NotDoneActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodetails);

        Bundle extras = getIntent().getExtras();
        String value1 = extras.getString("id");
        String value2 = extras.getString("taskName");
        value1 = value1 + " -> " + value2;
        TextView tv = (TextView)findViewById(R.id.habitName);
        tv.setText(value1);
    }

    public void onUpdateButtonClick(View view) {
        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();
    }
}


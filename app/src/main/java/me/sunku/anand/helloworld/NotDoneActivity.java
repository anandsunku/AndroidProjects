package me.sunku.anand.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.sunku.anand.helloworld.R;

/**
 * Created by anand on 14/08/16.
 */

public class NotDoneActivity extends Activity {
    SQLiteDatabase db;
    String taskid,taskname, reasonTitle, reasonDesc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodetails);

        Bundle extras = getIntent().getExtras();
        taskid = extras.getString("id");
        taskname = extras.getString("taskName");
        String title;
        title = taskid + " -> " + taskname;
        TextView tv = (TextView)findViewById(R.id.habitName);
        tv.setText(title);

        db = openOrCreateDatabase("me.sunku.anand.androidtodo", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS NotDoneActivity(habitid VARCHAR, " +
                "habitname VARCHAR, ReasonTitle VARCHAR, ReasonDesc VARCHAR, TimeStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP );");
    }

    public void onUpdateButtonClick(View view) {

        //collect all the details
        //editReason, editNotReasonDetailText
        reasonTitle = ((TextView)findViewById(R.id.editReason)).getText().toString();
        reasonDesc = ((TextView)findViewById(R.id.editNotReasonDetailText)).getText().toString();

        String insertQueryStr;

        insertQueryStr = "INSERT INTO NotDoneActivity VALUES('"+taskid+"',"+
                "'"+taskname+"'," +
                "'"+reasonTitle+"'," +
                "'"+reasonDesc+"'," +
                "DATETIME('now'));";

        db.execSQL(insertQueryStr);

        Intent i = new Intent();
        i.putExtra("taskid",taskid);
        setResult(RESULT_OK,i);
        finish();
    }
}


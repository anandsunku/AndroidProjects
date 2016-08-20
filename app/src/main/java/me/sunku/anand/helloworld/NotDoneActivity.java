package me.sunku.anand.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

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

        //populate the reasons array with unique reasons from the database table.
        String RTQuery = "SELECT DISTINCT ReasonTitle from NotDoneActivity where habitid='"+taskid+"'";
        Cursor cur =  db.rawQuery(RTQuery,new String[]{});
        ArrayList<String> array = new ArrayList<String>();
        while (cur.moveToNext()){
            String t1 = cur.getString(cur.getColumnIndex("ReasonTitle"));
            array.add(t1);
        }

        String[] reasonTitles = array.toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,reasonTitles);
        AutoCompleteTextView actv = (AutoCompleteTextView)findViewById(R.id.editReason);
        actv.setThreshold(1);
        actv.setAdapter(adapter);
        actv.setTextColor(Color.RED);
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


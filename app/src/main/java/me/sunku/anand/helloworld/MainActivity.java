package me.sunku.anand.helloworld;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Created by anand on 06/08/16.
 */

public class MainActivity extends Activity{

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("studentsDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR);");
        db.execSQL("INSERT INTO student VALUES('simple');");
    }
}

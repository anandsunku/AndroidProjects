package me.sunku.anand.helloworld;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by anand on 06/08/16.
 */

/* added to github with the below link
* https://www.londonappdeveloper.com/how-to-use-git-hub-with-android-studio/
* */
public class MainActivity extends Activity implements View.OnClickListener{

    SQLiteDatabase db;
    Button btnViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewAll = (Button)findViewById(R.id.btnViewAll);
        btnViewAll.setOnClickListener(this);

        db = openOrCreateDatabase("studentsDB", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR);");
        db.execSQL("INSERT INTO student VALUES('simple');");
    }

    @Override
    public void onClick(View v) {
        if(v == btnViewAll){
            Cursor c= db.rawQuery("SELECT * FROM student",null);
            if(c.getCount() == 0)
            {
                showMessage("Error","No records found");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while(c.moveToNext())
            {
                buffer.append("Rollno: "+ c.getString(0)+"\n");
            }
            showMessage("StudentDetails", buffer.toString());
        }
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}

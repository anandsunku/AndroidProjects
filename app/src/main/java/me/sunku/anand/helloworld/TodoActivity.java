package me.sunku.anand.helloworld;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by anand on 13/08/16.
 */

public class TodoActivity extends ListActivity {
    private ListAdapter todoListAdapter;
    private TodoListSQLHelper todoListSQLHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        verifyStoragePermissions(this);
        updateTodoList();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.layout.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.action_add_task:
                AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(this);
                todoTaskBuilder.setTitle("Add Todo Task Item");
                todoTaskBuilder.setMessage("describe the Todo task...");
                final EditText todoET = new EditText(this);
                todoTaskBuilder.setView(todoET);
                todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String todoTaskInput = todoET.getText().toString();
                        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
                        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.clear();

                        //write the To-do task input into database table
                        values.put(TodoListSQLHelper.COL1_TASK, todoTaskInput);
                        values.put(TodoListSQLHelper.COL2_DONE, "0");
                        values.put(TodoListSQLHelper.COL3_NOTDONE, "0");
                        sqLiteDatabase.insertWithOnConflict(TodoListSQLHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                        //update the To-do task list UI
                        updateTodoList();
                    }
                });

                todoTaskBuilder.setNegativeButton("Cancel", null);

                todoTaskBuilder.create().show();
                return true;
            case R.id.action_backup_db:
                try {
                    File myFile = new File("mnt/sdcard/file.txt");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append("test");
                    myOutWriter.close();
                    fOut.close();

                    Toast.makeText(getApplicationContext(),"mnt/sdcard/file.txt" + " saved successfully !!",Toast.LENGTH_LONG).show();

                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_backup_db_real:
                File path=getApplicationContext().getDatabasePath("me.sunku.anand.androidtodo");
                String db_path=path.getAbsolutePath();

                //String strDabasePath = getApplicationContext().getDatabasePath("me.sunku.anand.androidtodo").toString();
                File f=new File(db_path);
                FileInputStream fis=null;
                FileOutputStream fos=null;

                try
                {
                    fis=new FileInputStream(f);
                    fos=new FileOutputStream("/mnt/sdcard/db_dump.db");
                    while(true)
                    {
                        int i=fis.read();
                        if(i!=-1)
                        {fos.write(i);}
                        else
                        {break;}
                    }
                    fos.flush();
                    Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
                }
                finally
                {
                    try
                    {
                        fos.close();
                        fis.close();
                    }
                    catch(IOException ioe)
                    {}
                }
                return true;
            default:
                return false;
        }
    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void updateTodoList()
    {
        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getReadableDatabase();

        final Cursor cursor = sqLiteDatabase.query(TodoListSQLHelper.TABLE_NAME,
                new String[]{todoListSQLHelper._ID, TodoListSQLHelper.COL1_TASK,
                        TodoListSQLHelper.COL4_LASTOPERATION,TodoListSQLHelper.COL2_DONE,
                        TodoListSQLHelper.COL3_NOTDONE
                },
                null,null,null,null,null);

        todoListAdapter = new SimpleCursorAdapter(this,
                R.layout.todotask,cursor, new String[]{TodoListSQLHelper.COL1_TASK,TodoListSQLHelper._ID},
                new int[]{R.id.todoTaskTV,R.id.todoId},0);

        // todo - update the color of the last operation.
        ((SimpleCursorAdapter)todoListAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            public boolean setViewValue(View view, Cursor cursor1, int coloumnIndex){
                if (view.getId() == R.id.todoTaskTV)
                {
                    int colid = cursor.getColumnIndex(TodoListSQLHelper.COL4_LASTOPERATION);
                    String status = cursor.getString(colid);
                    if (status != null) {
                        if(status.equals("done")){
                            ((View)view.getParent()).setBackgroundColor(Color.GREEN);
                            ((TextView)view).setTextColor(Color.BLACK);
                        }
                        if(status.equals("notdone")){
                            ((View)view.getParent()).setBackgroundColor(Color.RED);
                            ((TextView)view).setTextColor(Color.BLACK);
                        }
                    }

                    int donecol = cursor.getColumnIndex(TodoListSQLHelper.COL2_DONE);
                    status = cursor.getString(donecol);
                    Button btnComp = (Button)((View)view.getParent().getParent()).findViewById(R.id.completeBtn);
                    btnComp.setText("Yes ("+status+")");

                    int notdonecol = cursor.getColumnIndex(TodoListSQLHelper.COL3_NOTDONE);
                    status = cursor.getString(notdonecol);
                    Button btnNotComp = (Button)((View)view.getParent().getParent()).findViewById(R.id.noCompleteBtn);
                    btnNotComp.setText("No ("+status+")");

                    return false;
                }
                return false;
            }
        });


        this.setListAdapter(todoListAdapter);
    }

    public void onDoneButtonClick(View view){
        SQLiteDatabase db;
        View v = (View) view.getParent().getParent();

        TextView todoTV = (TextView) v.findViewById(R.id.todoId);
        String todoTaskItem = todoTV.getText().toString();

        TextView todoTVText = (TextView) v.findViewById(R.id.todoTaskTV);
        String todoTaskName = todoTVText.getText().toString();

        db = openOrCreateDatabase("me.sunku.anand.androidtodo", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS DoneActivity(habitid VARCHAR, " +
                "habitname VARCHAR, TimeStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP );");
        String insertQueryStr;
        insertQueryStr = "INSERT INTO DoneActivity VALUES('"+todoTaskItem+"',"+
                "'"+todoTaskName+"'," +
                "DATETIME('now'));";
        // insert the log entry of done status
        db.execSQL(insertQueryStr);


        //todo : udpate in the main todolist table with count
        //todo : update the color of the done operation in the table.
        String upCntDoneSql = "UPDATE " + todoListSQLHelper.TABLE_NAME +
                " SET " + todoListSQLHelper.COL2_DONE + " = " + todoListSQLHelper.COL2_DONE + "+1" +
                " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem + "'";

        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
        SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
        sqlDB.execSQL(upCntDoneSql);

        String updateColor = "UPDATE " + todoListSQLHelper.TABLE_NAME +
                " SET "+ todoListSQLHelper.COL4_LASTOPERATION + " = 'done'" +
                " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem +"'";
        sqlDB.execSQL(updateColor);

        updateTodoList();
    }

    public void onNoDoneButtonClick(View view){
        //i want to know the id and name of the habit
        View v = (View) view.getParent().getParent();

        TextView todoTV = (TextView) v.findViewById(R.id.todoId);
        String todoTaskId = todoTV.getText().toString();

        TextView todoTVText = (TextView) v.findViewById(R.id.todoTaskTV);
        String todoTaskName = todoTVText.getText().toString();

        //todo : update the not done operation color in the db.

        // i would like to show no done details page here.
        Intent i = new Intent(getApplicationContext(), NotDoneActivity.class);
        i.putExtra("id", todoTaskId);
        i.putExtra("taskName", todoTaskName);
        // Set the request code to any code you like, you can identify the
        // callback via this code
        startActivityForResult(i,1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
            {
                String todoTaskItem=data.getStringExtra("taskid");

                //update the database with not done
                String upCntDoneSql = "UPDATE " + todoListSQLHelper.TABLE_NAME +
                        " SET " + todoListSQLHelper.COL3_NOTDONE + " = " + todoListSQLHelper.COL3_NOTDONE + "+1" +
                        " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem + "'";

                todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
                SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
                sqlDB.execSQL(upCntDoneSql);

                String updateColor = "UPDATE " + todoListSQLHelper.TABLE_NAME +
                        " SET "+ todoListSQLHelper.COL4_LASTOPERATION + " = 'notdone'" +
                        " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem +"'";
                sqlDB.execSQL(updateColor);
            }
            updateTodoList();
        }
    }

    public void onDelButtonClick(View view){
        View v = (View) view.getParent().getParent();
        TextView todoTV = (TextView) v.findViewById(R.id.todoId);
        String todoTaskItem = todoTV.getText().toString();

        String deleteTodoItemSql = "DELETE FROM " + todoListSQLHelper.TABLE_NAME +
                " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem  + "'";

        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
        SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
        sqlDB.execSQL(deleteTodoItemSql);
        updateTodoList();
    }


}

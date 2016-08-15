package me.sunku.anand.helloworld;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by anand on 13/08/16.
 */

public class TodoActivity extends ListActivity {
    private ListAdapter todoListAdapter;
    private TodoListSQLHelper todoListSQLHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
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

                        //write the Todo task input into database table
                        values.put(TodoListSQLHelper.COL1_TASK, todoTaskInput);
                        sqLiteDatabase.insertWithOnConflict(TodoListSQLHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                        //update the Todo task list UI
                        updateTodoList();
                    }
                });

                todoTaskBuilder.setNegativeButton("Cancel", null);

                todoTaskBuilder.create().show();
                return true;
            default:
                return false;
        }
    }

    private void updateTodoList() {
        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TodoListSQLHelper.TABLE_NAME,
                new String[]{todoListSQLHelper._ID, TodoListSQLHelper.COL1_TASK},
                null,null,null,null,null);

        todoListAdapter = new SimpleCursorAdapter(this,
                R.layout.todotask,cursor, new String[]{TodoListSQLHelper.COL1_TASK,TodoListSQLHelper._ID},
                new int[]{R.id.todoTaskTV,R.id.todoId},0);

        this.setListAdapter(todoListAdapter);
    }

    public void onDoneButtonClick(View view){
        View v = (View) view.getParent();
        TextView todoTV = (TextView) v.findViewById(R.id.todoId);
        String todoTaskItem = todoTV.getText().toString();


        String deleteTodoItemSql = "DELETE FROM " + todoListSQLHelper.TABLE_NAME +
                " WHERE " + TodoListSQLHelper._ID + " = '" + todoTaskItem  + "'";

        todoListSQLHelper = new TodoListSQLHelper(TodoActivity.this);
        SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
        sqlDB.execSQL(deleteTodoItemSql);
        updateTodoList();
    }

    public void onNoDoneButtonClick(View view){
        //i want to know the id and name of the habit
        View v = (View) view.getParent();

        TextView todoTV = (TextView) v.findViewById(R.id.todoId);
        String todoTaskId = todoTV.getText().toString();

        TextView todoTVText = (TextView) v.findViewById(R.id.todoTaskTV);
        String todoTaskName = todoTVText.getText().toString();

        // i would like to show no done details page here.
        Intent i = new Intent(getApplicationContext(), NotDoneActivity.class);
        i.putExtra("id", todoTaskId);
        i.putExtra("taskName", todoTaskName);
        // Set the request code to any code you like, you can identify the
        // callback via this code
        startActivity(i);

    }
}

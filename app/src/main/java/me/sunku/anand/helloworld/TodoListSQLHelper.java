package me.sunku.anand.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by anand on 13/08/16.
 */

public class TodoListSQLHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "me.sunku.anand.androidtodo";
    public static final String TABLE_NAME = "TODO_LIST";
    public static final String COL1_TASK = "todo";
    public static final String COL2_DONE = "done";
    public static final String COL3_NOTDONE = "notdone";
    public static final String COL4_LASTOPERATION = "lastoperation";
    public static final String _ID = BaseColumns._ID;

    public TodoListSQLHelper(Context context){
        super(context, DB_NAME, null,2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTodoListTable = "CREATE TABLE "+ TABLE_NAME +" ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL1_TASK + " TEXT, " +
                COL2_DONE + " INTEGER, " + //count of number of done times
                COL3_NOTDONE + " INTEGER," + // count of number of not done times
                COL4_LASTOPERATION + " TEXT)"; // what was your last attempt.
        db.execSQL(createTodoListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
}
